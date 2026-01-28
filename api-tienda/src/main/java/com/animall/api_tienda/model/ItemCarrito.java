package com.animall.api_tienda.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Item dentro de un carrito de compras.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "item_carrito")
public class ItemCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_carrito", updatable = false, nullable = false)
    private Long id;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull
    @Min(0)
    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    @ManyToOne
    @JoinColumn(name = "id_carrito", nullable = false)
    private Carrito carrito;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
}

