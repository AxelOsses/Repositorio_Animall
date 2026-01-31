package com.animall.api_tienda.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animall.api_tienda.model.MetodoPago;
import com.animall.api_tienda.model.Usuario;
import com.animall.api_tienda.repository.MetodoPagoRepository;
import com.animall.api_tienda.repository.UsuarioRepository;
import com.animall.api_tienda.service.MetodoPagoService;

/**
 * Servicio para gestión de métodos de pago de usuario.
 * 
 * Nota: Eliminar un método de pago NO afecta a los pedidos existentes porque
 * el Pedido almacena un SNAPSHOT del método de pago al momento de la compra.
 */
@Service
@Transactional
public class MetodoPagoServiceImpl implements MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;
    private final UsuarioRepository usuarioRepository;

    public MetodoPagoServiceImpl(MetodoPagoRepository metodoPagoRepository,
                                 UsuarioRepository usuarioRepository) {
        this.metodoPagoRepository = metodoPagoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetodoPago> listarPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id " + usuarioId));
        return metodoPagoRepository.findByUsuario(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MetodoPago> buscarPorId(Long id) {
        return metodoPagoRepository.findById(id);
    }

    @Override
    public MetodoPago crear(Long usuarioId, MetodoPago metodoPago) {
        if (metodoPago == null) {
            throw new IllegalArgumentException("El método de pago no puede ser null");
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id " + usuarioId));
        metodoPago.setId(null);
        metodoPago.setUsuario(usuario);
        return metodoPagoRepository.save(metodoPago);
    }

    @Override
    public MetodoPago actualizar(Long id, Long usuarioId, MetodoPago metodoPago) {
        if (metodoPago == null) {
            throw new IllegalArgumentException("El método de pago no puede ser null");
        }
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id " + usuarioId));
        return metodoPagoRepository.findById(id)
                .filter(m -> m.getUsuario().getId().equals(usuarioId))
                .map(actual -> {
                    actual.setTipo(metodoPago.getTipo());
                    actual.setAlias(metodoPago.getAlias());
                    return metodoPagoRepository.save(actual);
                })
                .orElseThrow(() -> new IllegalArgumentException("Método de pago no encontrado con id " + id + " para el usuario"));
    }

    @Override
    public void eliminar(Long id, Long usuarioId) {
        MetodoPago metodoPago = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Método de pago no encontrado con id " + id));
        if (!metodoPago.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("El método de pago no pertenece al usuario");
        }
        // Se puede eliminar libremente porque los pedidos tienen snapshot del método de pago
        metodoPagoRepository.delete(metodoPago);
    }
}
