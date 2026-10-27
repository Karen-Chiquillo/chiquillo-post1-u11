package com.universidad.refactoringu11.reportes;

import com.universidad.refactoringu11.inventario.InventarioService;
import com.universidad.refactoringu11.inventario.Producto;
import com.universidad.refactoringu11.inventario.TipoProducto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

// Con @Autowired(required = false), si Spring no conectara el notificador las alertas
// se perderian sin ningun error; esta prueba verifica la conexion real entre los beans
@SpringBootTest
class NotificacionBajoStockIntegracionTest {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private ReporteBajoStockService reporteBajoStockService;

    @Test
    void springConectaElReporteComoNotificadorDelInventario() {
        inventarioService.agregarProducto(new Producto(1L, "Clean Code", TipoProducto.LIBRO, 45.0, 20, 5));

        inventarioService.actualizarInventario(1L, 17, "SALIDA");

        assertTrue(reporteBajoStockService.generarReporte().contains("Clean Code: stock 3 (minimo 5), reordenar 12"));
    }
}
