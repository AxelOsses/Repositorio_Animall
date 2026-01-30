package com.animall.api_tienda.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animall.api_tienda.model.MetodoPago;
import com.animall.api_tienda.model.Usuario;
import com.animall.api_tienda.repository.MetodoPagoRepository;
import com.animall.api_tienda.repository.PedidoRepository;
import com.animall.api_tienda.repository.UsuarioRepository;
import com.animall.api_tienda.service.MetodoPagoService;

@Service
@Transactional
public class MetodoPagoServiceImpl implements MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;

    public MetodoPagoServiceImpl(MetodoPagoRepository metodoPagoRepository,
                                 UsuarioRepository usuarioRepository,
                                 PedidoRepository pedidoRepository) {
        this.metodoPagoRepository = metodoPagoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
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
        if (pedidoRepository.existsByMetodoPago(metodoPago)) {
            throw new IllegalStateException("No se puede eliminar el método de pago porque está asociado a uno o más pedidos.");
        }
        metodoPagoRepository.delete(metodoPago);
    }
}
