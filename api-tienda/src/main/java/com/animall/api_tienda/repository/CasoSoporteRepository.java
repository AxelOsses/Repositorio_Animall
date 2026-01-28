package com.animall.api_tienda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.animall.api_tienda.model.CasoSoporte;
import com.animall.api_tienda.model.Usuario;

public interface CasoSoporteRepository extends JpaRepository<CasoSoporte, Long> {

    List<CasoSoporte> findByUsuario(Usuario usuario);
}

