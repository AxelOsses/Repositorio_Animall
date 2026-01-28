package com.animall.api_tienda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.animall.api_tienda.model.DetallePedido;
import com.animall.api_tienda.model.Pedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    List<DetallePedido> findByPedido(Pedido pedido);
}

