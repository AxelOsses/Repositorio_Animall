package com.animall.api_tienda.service;

import java.util.List;
import java.util.Optional;

import com.animall.api_tienda.model.Producto;

public interface ProductoService {

    List<Producto> listarTodos();

    Optional<Producto> buscarPorId(Long id);

    Optional<Producto> buscarPorNombre(String nombre);

    List<Producto> listarPorCategoria(Long idCategoria);

    Producto crear(Producto producto, Long idCategoria);

    Producto actualizar(Long id, Producto producto, Long idCategoria);

    void eliminar(Long id);
}

