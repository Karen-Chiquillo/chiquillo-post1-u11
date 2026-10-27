package com.universidad.refactoringu11.reportes;

import com.universidad.refactoringu11.inventario.InventarioService;
import com.universidad.refactoringu11.inventario.Producto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReporteBajoStockService {

    private final InventarioService inventarioService;

    public ReporteBajoStockService(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    public String generarReporte() {
        List<Producto> productosBajoStock = inventarioService.listarProductosBajoStock();
        if (productosBajoStock.isEmpty()) return "Reporte de bajo stock: sin productos bajo el minimo";

        StringBuilder reporte = new StringBuilder("Reporte de bajo stock:");
        for (Producto producto : productosBajoStock) {
            reporte.append("\n- ").append(producto.getNombre())
                    .append(": stock ").append(producto.getStock())
                    .append(" (minimo ").append(producto.getStockMinimo()).append(")")
                    .append(", reordenar ").append(producto.calcularCantidadReorden());
        }
        return reporte.toString();
    }
}
