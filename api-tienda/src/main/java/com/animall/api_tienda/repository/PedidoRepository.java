package com.animall.api_tienda.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.animall.api_tienda.model.Pedido;
import com.animall.api_tienda.model.Usuario;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuario(Usuario usuario);

    /**
     * Obtiene pedidos del usuario con sus detalles cargados (eager fetch).
     * Ya no hay JOIN con Producto porque DetallePedido ahora es un snapshot.
     */
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.detalles WHERE p.usuario = :usuario")
    List<Pedido> findByUsuarioWithDetalles(@Param("usuario") Usuario usuario);

    /**
     * Obtiene un pedido por ID con sus detalles cargados.
     */
    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.detalles WHERE p.id = :id")
    Optional<Pedido> findByIdWithDetalles(@Param("id") Long id);
}
