package com.animall.api_tienda.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.animall.api_tienda.model.Favorito;
import com.animall.api_tienda.model.Producto;
import com.animall.api_tienda.model.Usuario;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    List<Favorito> findByUsuario(Usuario usuario);

    Optional<Favorito> findByUsuarioAndProducto(Usuario usuario, Producto producto);
}

