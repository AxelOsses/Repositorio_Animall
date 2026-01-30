package com.animall.api_tienda.service;

import java.util.List;
import java.util.Optional;

import com.animall.api_tienda.dto.ProductoCreateRequest;
import com.animall.api_tienda.dto.ProductoUpdateRequest;
import com.animall.api_tienda.model.Producto;

public interface ProductoService {

    List<Producto> listarTodos();

    Optional<Producto> buscarPorId(Long id);

    Optional<Producto> buscarPorNombre(String nombre);

    List<Producto> listarPorCategoria(Long idCategoria);

    Producto crear(ProductoCreateRequest request);

    Producto actualizar(Long id, ProductoUpdateRequest request);

    void eliminar(Long id);
}

