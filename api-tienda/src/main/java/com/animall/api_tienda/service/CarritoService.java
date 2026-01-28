package com.animall.api_tienda.service;

import com.animall.api_tienda.model.Carrito;

public interface CarritoService {

    Carrito obtenerOCrearCarritoParaUsuario(Long usuarioId);

    Carrito agregarProducto(Long usuarioId, Long productoId, int cantidad);

    Carrito actualizarCantidadItem(Long usuarioId, Long itemCarritoId, int nuevaCantidad);

    Carrito eliminarItem(Long usuarioId, Long itemCarritoId);

    Carrito vaciarCarrito(Long usuarioId);
}

