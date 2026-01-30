package com.animall.api_tienda.service;

import java.util.List;
import java.util.Optional;

import com.animall.api_tienda.dto.UsuarioCreateRequest;
import com.animall.api_tienda.dto.UsuarioUpdateRequest;
import com.animall.api_tienda.model.Usuario;

public interface UsuarioService {

    List<Usuario> listarTodos();

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorEmail(String email);

    Usuario crear(UsuarioCreateRequest request);

    Usuario actualizar(Long id, UsuarioUpdateRequest request);

    void eliminar(Long id);
}

