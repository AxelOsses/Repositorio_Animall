package com.animall.api_tienda.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animall.api_tienda.model.Usuario;
import com.animall.api_tienda.repository.UsuarioRepository;
import com.animall.api_tienda.service.UsuarioService;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public Usuario crear(Usuario usuario) {
        usuario.setId(null);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario actualizar(Long id, Usuario usuario) {
        return usuarioRepository.findById(id)
                .map(actual -> {
                    actual.setNombre(usuario.getNombre());
                    actual.setEmail(usuario.getEmail());
                    actual.setPassword(usuario.getPassword());
                    actual.setPuntos(usuario.getPuntos());
                    actual.setAhorroTotal(usuario.getAhorroTotal());
                    actual.setRol(usuario.getRol());
                    actual.setConfiguracion(usuario.getConfiguracion());
                    return usuarioRepository.save(actual);
                })
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id " + id));
    }

    @Override
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
}

