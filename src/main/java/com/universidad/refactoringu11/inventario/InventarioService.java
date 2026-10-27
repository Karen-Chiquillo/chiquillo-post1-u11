package com.universidad.refactoringu11.inventario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InventarioService {

    private static final String ENTRADA = "ENTRADA";
    private static final String SALIDA = "SALIDA";

    private final List<Producto> productos = new ArrayList<>();
    private final List<String> movimientos = new ArrayList<>();

    private NotificadorBajoStock notificador;

    @Autowired(required = false)
    public void setNotificador(NotificadorBajoStock notificador) {
        this.notificador = notificador;
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public String actualizarInventario(Long productoId, int cantidad, String tipoMovimiento) {
        if (!sonDatosValidos(productoId, cantidad, tipoMovimiento)) return "ERROR_DATOS_INVALIDOS";
        return buscarProducto(productoId)
                .map(producto -> aplicarMovimiento(producto, cantidad, tipoMovimiento))
                .orElse("ERROR_PRODUCTO_NO_EXISTE");
    }

    private boolean sonDatosValidos(Long productoId, int cantidad, String tipoMovimiento) {
        return productoId != null && cantidad != 0 && esTipoMovimientoValido(tipoMovimiento);
    }

    private boolean esTipoMovimientoValido(String tipoMovimiento) {
        return tipoMovimiento.equals(ENTRADA) || tipoMovimiento.equals(SALIDA);
    }

    private Optional<Producto> buscarProducto(Long productoId) {
        return productos.stream()
                .filter(producto -> producto.getId().equals(productoId))
                .findFirst();
    }

    private String aplicarMovimiento(Producto producto, int cantidad, String tipoMovimiento) {
        int nuevoStock = calcularNuevoStock(producto.getStock(), cantidad, tipoMovimiento);
        if (tipoMovimiento.equals(SALIDA) && nuevoStock < 0) return "ERROR_STOCK_INSUFICIENTE";
        producto.setStock(nuevoStock);
        registrarMovimiento(producto, cantidad, tipoMovimiento);
        return producto.necesitaReorden() ? alertarReorden(producto) : "OK_" + nuevoStock;
    }

    private int calcularNuevoStock(int stockActual, int cantidad, String tipoMovimiento) {
        return tipoMovimiento.equals(ENTRADA) ? stockActual + cantidad : stockActual - cantidad;
    }

    private void registrarMovimiento(Producto producto, int cantidad, String tipoMovimiento) {
        String registro = tipoMovimiento + " de " + cantidad + " unidades de "
                + producto.getNombre() + " -> stock final: " + producto.getStock();
        movimientos.add(registro);
        System.out.println(registro);
    }

    private String alertarReorden(Producto producto) {
        int cantidadReorden = producto.calcularCantidadReorden();
        if (notificador != null) {
            notificador.notificar(producto, cantidadReorden);
        }
        System.out.println("ALERTA: " + producto.getNombre()
                + " bajo stock minimo. Reordenar " + cantidadReorden + " unidades.");
        return "OK_BAJO_STOCK_REORDEN_" + cantidadReorden;
    }
}
