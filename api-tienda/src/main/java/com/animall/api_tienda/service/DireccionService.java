package com.animall.api_tienda.service;

import java.util.List;
import java.util.Optional;

import com.animall.api_tienda.model.Direccion;

public interface DireccionService {

    List<Direccion> listarPorUsuario(Long usuarioId);

    Optional<Direccion> buscarPorId(Long id);

    Direccion crear(Long usuarioId, Direccion direccion);

    Direccion actualizar(Long id, Long usuarioId, Direccion direccion);

    void eliminar(Long id, Long usuarioId);
}
