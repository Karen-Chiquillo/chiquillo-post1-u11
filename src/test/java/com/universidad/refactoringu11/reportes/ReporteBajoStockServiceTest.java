package com.universidad.refactoringu11.reportes;

import com.universidad.refactoringu11.inventario.InventarioService;
import com.universidad.refactoringu11.inventario.Producto;
import com.universidad.refactoringu11.inventario.TipoProducto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReporteBajoStockServiceTest {

    private final ReporteBajoStockService reporte = new ReporteBajoStockService();

    @Test
    void sinAlertas_indicaQueNoHayProductosBajoElMinimo() {
        assertEquals("Reporte de bajo stock: sin productos bajo el minimo", reporte.generarReporte());
    }

    @Test
    void notificar_registraLaAlertaEnElReporte() {
        Producto libro = new Producto(1L, "Clean Code", TipoProducto.LIBRO, 45.0, 3, 5);
        reporte.notificar(libro, 12);
        assertEquals("Reporte de bajo stock:\n- Clean Code: stock 3 (minimo 5), reordenar 12",
                reporte.generarReporte());
    }

    @Test
    void inventarioConNotificador_avisaAlReporteCuandoQuedaBajoStock() {
        InventarioService inventario = new InventarioService();
        inventario.setNotificador(reporte);
        inventario.agregarProducto(new Producto(1L, "Clean Code", TipoProducto.LIBRO, 45.0, 20, 5));
        inventario.agregarProducto(new Producto(2L, "Teclado", TipoProducto.ELECTRONICO, 100.0, 10, 2));

        inventario.actualizarInventario(1L, 17, "SALIDA");
        inventario.actualizarInventario(2L, 1, "SALIDA");

        assertEquals("Reporte de bajo stock:\n- Clean Code: stock 3 (minimo 5), reordenar 12",
                reporte.generarReporte());
    }
}
