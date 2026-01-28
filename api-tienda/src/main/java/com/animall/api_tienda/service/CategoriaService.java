package com.animall.api_tienda.service;

import java.util.List;
import java.util.Optional;

import com.animall.api_tienda.model.Categoria;

public interface CategoriaService {

    List<Categoria> listarTodas();

    Optional<Categoria> buscarPorId(Long id);

    Optional<Categoria> buscarPorNombre(String nombre);

    Categoria crear(Categoria categoria);

    Categoria actualizar(Long id, Categoria categoria);

    void eliminar(Long id);
}

