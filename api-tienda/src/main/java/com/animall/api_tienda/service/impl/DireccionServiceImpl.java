package com.animall.api_tienda.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animall.api_tienda.model.Direccion;
import com.animall.api_tienda.model.Usuario;
import com.animall.api_tienda.repository.DireccionRepository;
import com.animall.api_tienda.repository.UsuarioRepository;
import com.animall.api_tienda.service.DireccionService;

@Service
@Transactional
public class DireccionServiceImpl implements DireccionService {

    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;

    public DireccionServiceImpl(DireccionRepository direccionRepository,
                                UsuarioRepository usuarioRepository) {
        this.direccionRepository = direccionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Direccion> listarPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id " + usuarioId));
        return direccionRepository.findByUsuario(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Direccion> buscarPorId(Long id) {
        return direccionRepository.findById(id);
    }

    @Override
    public Direccion crear(Long usuarioId, Direccion direccion) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id " + usuarioId));
        direccion.setId(null);
        direccion.setUsuario(usuario);
        return direccionRepository.save(direccion);
    }

    @Override
    public Direccion actualizar(Long id, Long usuarioId, Direccion direccion) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id " + usuarioId));
        return direccionRepository.findById(id)
                .filter(d -> d.getUsuario().getId().equals(usuarioId))
                .map(actual -> {
                    actual.setRegion(direccion.getRegion());
                    actual.setComuna(direccion.getComuna());
                    actual.setDireccion(direccion.getDireccion());
                    return direccionRepository.save(actual);
                })
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada con id " + id + " para el usuario"));
    }

    @Override
    public void eliminar(Long id, Long usuarioId) {
        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada con id " + id));
        if (!direccion.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("La dirección no pertenece al usuario");
        }
        direccionRepository.delete(direccion);
    }
}
