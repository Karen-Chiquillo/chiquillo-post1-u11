package com.universidad.refactoringu11.inventario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Casos que el refactor del Long Method podia cambiar sin que ninguna prueba de
// caracterizacion fallara; pasan igual sobre el codigo original (commit 9f5752b)
class InventarioServiceRegresionTest {

    private InventarioService service;
    private Producto libro;

    @BeforeEach
    void setUp() {
        service = new InventarioService();
        libro = new Producto(1L, "Clean Code", TipoProducto.LIBRO, 45.0, 20, 5);
        service.agregarProducto(libro);
    }

    @Test
    void productoIdYTipoNulos_retornaErrorSinLanzarExcepcion() {
        assertEquals("ERROR_DATOS_INVALIDOS", service.actualizarInventario(null, 5, null));
    }

    @Test
    void cantidadCeroYTipoNulo_retornaErrorSinLanzarExcepcion() {
        assertEquals("ERROR_DATOS_INVALIDOS", service.actualizarInventario(1L, 0, null));
    }

    @Test
    void entradaNegativaMayorQueElStock_dejaStockNegativoSinError() {
        // stock 20 + (-30) = -10: solo SALIDA rechaza el stock negativo
        assertEquals("OK_BAJO_STOCK_REORDEN_25", service.actualizarInventario(1L, -30, "ENTRADA"));
        assertEquals(-10, libro.getStock());
    }
}
