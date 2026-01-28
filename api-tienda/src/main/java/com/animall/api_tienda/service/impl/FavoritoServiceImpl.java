package com.animall.api_tienda.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animall.api_tienda.model.Favorito;
import com.animall.api_tienda.model.Producto;
import com.animall.api_tienda.model.Usuario;
import com.animall.api_tienda.repository.FavoritoRepository;
import com.animall.api_tienda.repository.ProductoRepository;
import com.animall.api_tienda.repository.UsuarioRepository;
import com.animall.api_tienda.service.FavoritoService;

@Service
@Transactional
public class FavoritoServiceImpl implements FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public FavoritoServiceImpl(FavoritoRepository favoritoRepository,
                               UsuarioRepository usuarioRepository,
                               ProductoRepository productoRepository) {
        this.favoritoRepository = favoritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favorito> listarPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id " + usuarioId));
        return favoritoRepository.findByUsuario(usuario);
    }

    @Override
    public Favorito agregarAFavoritos(Long usuarioId, Long productoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id " + usuarioId));
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id " + productoId));

        return favoritoRepository.findByUsuarioAndProducto(usuario, producto)
                .orElseGet(() -> {
                    Favorito favorito = new Favorito();
                    favorito.setUsuario(usuario);
                    favorito.setProducto(producto);
                    return favoritoRepository.save(favorito);
                });
    }

    @Override
    public void eliminarDeFavoritos(Long usuarioId, Long productoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id " + usuarioId));
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id " + productoId));

        favoritoRepository.findByUsuarioAndProducto(usuario, producto)
                .ifPresent(favoritoRepository::delete);
    }
}

