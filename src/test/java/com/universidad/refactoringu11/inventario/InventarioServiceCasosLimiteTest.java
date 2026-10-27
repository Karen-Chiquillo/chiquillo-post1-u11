package com.universidad.refactoringu11.inventario;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Casos limite y salida por consola de actualizarInventario: documentan lo que el
// codigo hace hoy, aunque no sea lo deseable (por ejemplo, cantidades negativas)
class InventarioServiceCasosLimiteTest {

    private InventarioService service;
    private Producto libro;
    private final ByteArrayOutputStream consola = new ByteArrayOutputStream();
    private PrintStream salidaOriginal;

    @BeforeEach
    void setUp() {
        service = new InventarioService();
        libro = new Producto(1L, "Clean Code", TipoProducto.LIBRO, 45.0, 20, 5);
        service.agregarProducto(libro);
        salidaOriginal = System.out;
        System.setOut(new PrintStream(consola, true));
    }

    @AfterEach
    void restaurarConsola() {
        System.setOut(salidaOriginal);
    }

    private List<String> lineasConsola() {
        return consola.toString().lines().toList();
    }

    @Test
    void productoIdNulo_retornaErrorDatosInvalidos() {
        assertEquals("ERROR_DATOS_INVALIDOS", service.actualizarInventario(null, 5, "ENTRADA"));
    }

    @Test
    void cantidadCero_retornaErrorDatosInvalidos() {
        assertEquals("ERROR_DATOS_INVALIDOS", service.actualizarInventario(1L, 0, "ENTRADA"));
    }

    @Test
    void tipoMovimientoEnMinusculas_retornaErrorDatosInvalidos() {
        assertEquals("ERROR_DATOS_INVALIDOS", service.actualizarInventario(1L, 5, "entrada"));
    }

    @Test
    void tipoMovimientoNulo_lanzaNullPointerException() {
        assertThrows(NullPointerException.class, () -> service.actualizarInventario(1L, 5, null));
    }

    @Test
    void datosInvalidos_seValidanAntesDeBuscarElProducto() {
        assertEquals("ERROR_DATOS_INVALIDOS", service.actualizarInventario(99L, 0, "ENTRADA"));
    }

    @Test
    void entradaConCantidadNegativa_descuentaStock() {
        assertEquals("OK_15", service.actualizarInventario(1L, -5, "ENTRADA"));
        assertEquals(15, libro.getStock());
    }

    @Test
    void salidaConCantidadNegativa_aumentaStock() {
        assertEquals("OK_25", service.actualizarInventario(1L, -5, "SALIDA"));
        assertEquals(25, libro.getStock());
    }

    @Test
    void salidaQueDejaStockIgualAlMinimo_noActivaReorden() {
        assertEquals("OK_5", service.actualizarInventario(1L, 15, "SALIDA"));
    }

    @Test
    void salidaDeTodoElStock_dejaCeroYActivaReorden() {
        assertEquals("OK_BAJO_STOCK_REORDEN_15", service.actualizarInventario(1L, 20, "SALIDA"));
        assertEquals(0, libro.getStock());
    }

    @Test
    void entradaQueNoAlcanzaElMinimo_tambienActivaReorden() {
        Producto cafe = new Producto(2L, "Cafe", TipoProducto.ALIMENTO, 15.0, 1, 5);
        service.agregarProducto(cafe);
        assertEquals("OK_BAJO_STOCK_REORDEN_12", service.actualizarInventario(2L, 2, "ENTRADA"));
        assertEquals(3, cafe.getStock());
    }

    @Test
    void conVariosProductos_soloModificaElSolicitado() {
        Producto cafe = new Producto(2L, "Cafe", TipoProducto.ALIMENTO, 15.0, 30, 5);
        service.agregarProducto(cafe);
        assertEquals("OK_20", service.actualizarInventario(2L, 10, "SALIDA"));
        assertEquals(20, libro.getStock());
    }

    @Test
    void movimientoExitoso_imprimeSoloElRegistro() {
        service.actualizarInventario(1L, 10, "ENTRADA");
        assertEquals(List.of("ENTRADA de 10 unidades de Clean Code -> stock final: 30"), lineasConsola());
    }

    @Test
    void movimientoBajoStock_imprimeRegistroYAlerta() {
        service.actualizarInventario(1L, 17, "SALIDA");
        assertEquals(List.of(
                "SALIDA de 17 unidades de Clean Code -> stock final: 3",
                "ALERTA: Clean Code bajo stock minimo. Reordenar 12 unidades."), lineasConsola());
    }

    @Test
    void movimientoRechazado_noImprimeNada() {
        service.actualizarInventario(1L, 50, "SALIDA");
        assertTrue(lineasConsola().isEmpty());
    }
}
