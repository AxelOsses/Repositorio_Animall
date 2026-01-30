package com.animall.api_tienda.service;

import java.util.List;
import java.util.Optional;

import com.animall.api_tienda.model.MetodoPago;

public interface MetodoPagoService {

    List<MetodoPago> listarPorUsuario(Long usuarioId);

    Optional<MetodoPago> buscarPorId(Long id);

    MetodoPago crear(Long usuarioId, MetodoPago metodoPago);

    MetodoPago actualizar(Long id, Long usuarioId, MetodoPago metodoPago);

    void eliminar(Long id, Long usuarioId);
}
