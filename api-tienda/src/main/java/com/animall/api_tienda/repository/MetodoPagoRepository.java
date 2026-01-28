package com.animall.api_tienda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.animall.api_tienda.model.MetodoPago;
import com.animall.api_tienda.model.Usuario;

public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {

    List<MetodoPago> findByUsuario(Usuario usuario);
}

