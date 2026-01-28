package com.animall.api_tienda.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.animall.api_tienda.model.CategoriaSoporte;

public interface CategoriaSoporteRepository extends JpaRepository<CategoriaSoporte, Long> {

    Optional<CategoriaSoporte> findByNombre(String nombre);
}

