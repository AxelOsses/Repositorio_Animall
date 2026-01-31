package com.animall.api_tienda.service;

import java.util.List;
import java.util.Optional;

import com.animall.api_tienda.model.Pedido;

public interface PedidoService {

    /**
     * Confirma un pedido tomando los items del carrito activo del usuario.
     * El frontend envía solo direccionId y metodoPagoId.
     * El backend:
     * - Obtiene ItemCarrito del carrito del usuario
     * - Valida stock disponible
     * - Calcula precioUnitario con descuento desde BD
     * - Crea DetallePedido
     * - Calcula el total del pedido
     * - Asigna estado inicial (CREADO)
     * - Actualiza stock de productos
     * - Limpia el carrito
     * - Actualiza puntos y ahorro del usuario
     */
    Pedido confirmarPedidoDesdeCarrito(Long usuarioId, Long direccionId, Long metodoPagoId);

    List<Pedido> listarPorUsuario(Long usuarioId);

    Optional<Pedido> buscarPorId(Long id);

    Pedido cambiarEstado(Long pedidoId, String nombreNuevoEstado, boolean esAdmin);
}

