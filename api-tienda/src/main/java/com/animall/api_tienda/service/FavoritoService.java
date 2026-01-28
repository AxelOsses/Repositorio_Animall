package com.animall.api_tienda.service;

import java.util.List;

import com.animall.api_tienda.model.Favorito;

public interface FavoritoService {

    List<Favorito> listarPorUsuario(Long usuarioId);

    Favorito agregarAFavoritos(Long usuarioId, Long productoId);

    void eliminarDeFavoritos(Long usuarioId, Long productoId);
}

