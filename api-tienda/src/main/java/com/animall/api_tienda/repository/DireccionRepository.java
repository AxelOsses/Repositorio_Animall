package com.animall.api_tienda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.animall.api_tienda.model.Direccion;
import com.animall.api_tienda.model.Usuario;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {

    List<Direccion> findByUsuario(Usuario usuario);
}

