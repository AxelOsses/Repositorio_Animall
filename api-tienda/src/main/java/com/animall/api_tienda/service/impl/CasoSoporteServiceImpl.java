package com.animall.api_tienda.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animall.api_tienda.model.CasoSoporte;
import com.animall.api_tienda.model.CategoriaSoporte;
import com.animall.api_tienda.model.EstadoCaso;
import com.animall.api_tienda.model.Usuario;
import com.animall.api_tienda.repository.CasoSoporteRepository;
import com.animall.api_tienda.repository.CategoriaSoporteRepository;
import com.animall.api_tienda.repository.EstadoCasoRepository;
import com.animall.api_tienda.repository.UsuarioRepository;
import com.animall.api_tienda.service.CasoSoporteService;

@Service
@Transactional
public class CasoSoporteServiceImpl implements CasoSoporteService {

    private static final String ESTADO_INICIAL = "ABIERTO";

    private final CasoSoporteRepository casoSoporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaSoporteRepository categoriaSoporteRepository;
    private final EstadoCasoRepository estadoCasoRepository;

    public CasoSoporteServiceImpl(CasoSoporteRepository casoSoporteRepository,
                                  UsuarioRepository usuarioRepository,
                                  CategoriaSoporteRepository categoriaSoporteRepository,
                                  EstadoCasoRepository estadoCasoRepository) {
        this.casoSoporteRepository = casoSoporteRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaSoporteRepository = categoriaSoporteRepository;
        this.estadoCasoRepository = estadoCasoRepository;
    }

    @Override
    public CasoSoporte crearCaso(Long usuarioId, Long categoriaSoporteId, String descripcion) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id " + usuarioId));

        CategoriaSoporte categoria = categoriaSoporteRepository.findById(categoriaSoporteId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría de soporte no encontrada con id " + categoriaSoporteId));

        EstadoCaso estadoInicial = estadoCasoRepository.findByNombre(ESTADO_INICIAL)
                .orElseThrow(() -> new IllegalStateException("No se encontró el estado de caso inicial: " + ESTADO_INICIAL));

        CasoSoporte caso = new CasoSoporte();
        caso.setUsuario(usuario);
        caso.setCategoriaSoporte(categoria);
        caso.setDescripcion(descripcion);
        caso.setEstadoCaso(estadoInicial);

        return casoSoporteRepository.save(caso);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CasoSoporte> listarPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id " + usuarioId));
        return casoSoporteRepository.findByUsuario(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CasoSoporte> buscarPorId(Long casoId) {
        return casoSoporteRepository.findById(casoId);
    }

    @Override
    public CasoSoporte cambiarEstado(Long casoId, String nuevoEstado, boolean esAdmin) {
        CasoSoporte caso = casoSoporteRepository.findById(casoId)
                .orElseThrow(() -> new IllegalArgumentException("Caso de soporte no encontrado con id " + casoId));

        if ("CERRADO".equalsIgnoreCase(caso.getEstadoCaso().getNombre())) {
            throw new IllegalStateException("Un caso cerrado no puede modificarse");
        }

        if (!esAdmin && "CERRADO".equalsIgnoreCase(nuevoEstado)) {
            throw new IllegalStateException("Solo un administrador puede cerrar un caso");
        }

        EstadoCaso estado = estadoCasoRepository.findByNombre(nuevoEstado)
                .orElseThrow(() -> new IllegalArgumentException("Estado de caso no encontrado: " + nuevoEstado));

        caso.setEstadoCaso(estado);
        return caso;
    }
}

