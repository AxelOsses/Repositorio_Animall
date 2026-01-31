package com.animall.api_tienda.service;

import java.util.List;
import java.util.Optional;

import com.animall.api_tienda.dto.CrearPedidoRequest;
import com.animall.api_tienda.model.Pedido;

public interface PedidoService {

    /**
     * Crea un pedido directamente desde el DTO (sin pasar por carrito).
     * Valida stock, calcula precios con descuento, asigna estado inicial.
     */
    Pedido crearPedidoDirecto(CrearPedidoRequest request);

    /**
     * Confirma un pedido tomando los items del carrito del usuario.
     */
    Pedido confirmarPedidoDesdeCarrito(Long usuarioId, Long direccionId, Long metodoPagoId);

    List<Pedido> listarPorUsuario(Long usuarioId);

    Optional<Pedido> buscarPorId(Long id);

    Pedido cambiarEstado(Long pedidoId, String nombreNuevoEstado, boolean esAdmin);
}

