package com.universidad.refactoringu11.inventario;

public interface NotificadorBajoStock {
    void notificar(Producto producto, int cantidadReorden);
}
