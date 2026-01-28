package com.animall.api_tienda.service;

import java.util.List;
import java.util.Optional;

import com.animall.api_tienda.model.CasoSoporte;

public interface CasoSoporteService {

    CasoSoporte crearCaso(Long usuarioId, Long categoriaSoporteId, String descripcion);

    List<CasoSoporte> listarPorUsuario(Long usuarioId);

    Optional<CasoSoporte> buscarPorId(Long casoId);

    CasoSoporte cambiarEstado(Long casoId, String nuevoEstado, boolean esAdmin);
}

