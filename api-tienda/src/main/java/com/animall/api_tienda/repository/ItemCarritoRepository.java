package com.animall.api_tienda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.animall.api_tienda.model.Carrito;
import com.animall.api_tienda.model.ItemCarrito;

public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    List<ItemCarrito> findByCarrito(Carrito carrito);
}

