package com.animall.api_tienda.service;

import java.util.List;
import java.util.Optional;

import com.animall.api_tienda.model.Pedido;

public interface PedidoService {

    Pedido confirmarPedidoDesdeCarrito(Long usuarioId, Long direccionId, Long metodoPagoId);

    List<Pedido> listarPorUsuario(Long usuarioId);

    Optional<Pedido> buscarPorId(Long id);

    Pedido cambiarEstado(Long pedidoId, String nombreNuevoEstado, boolean esAdmin);
}

