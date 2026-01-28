package com.animall.api_tienda.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.animall.api_tienda.model.EstadoCaso;

public interface EstadoCasoRepository extends JpaRepository<EstadoCaso, Long> {

    Optional<EstadoCaso> findByNombre(String nombre);
}

