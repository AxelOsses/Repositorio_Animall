package com.animall.api_tienda.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.animall.api_tienda.model.Direccion;
import com.animall.api_tienda.model.MetodoPago;
import com.animall.api_tienda.model.Pedido;
import com.animall.api_tienda.model.Usuario;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuario(Usuario usuario);

    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.producto WHERE p.usuario = :usuario")
    List<Pedido> findByUsuarioWithDetalles(@Param("usuario") Usuario usuario);

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.detalles d LEFT JOIN FETCH d.producto WHERE p.id = :id")
    Optional<Pedido> findByIdWithDetalles(@Param("id") Long id);

    boolean existsByDireccion(Direccion direccion);

    boolean existsByMetodoPago(MetodoPago metodoPago);
}

