package com.animall.api_tienda.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Producto disponible en la tienda.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto", updatable = false, nullable = false)
    private Long id;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String nombre;

    @Size(max = 500)
    @Column(length = 500)
    private String descripcion;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Double precio;

    @NotNull
    @Min(0)
    @Column(name = "porcentaje_descuento", nullable = false)
    private Integer porcentajeDescuento = 0;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer stock;

    /**
     * URL o enlace de referencia a la imagen del producto.
     */
    @Size(max = 500)
    @Column(name = "imagen", length = 500)
    private String imagen;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @OneToMany(mappedBy = "producto")
    @JsonIgnore
    private List<Favorito> favoritos = new ArrayList<>();

    @OneToMany(mappedBy = "producto")
    @JsonIgnore
    private List<ItemCarrito> itemsCarrito = new ArrayList<>();

    // ❌ ELIMINADO: @OneToMany(mappedBy = "producto") List<DetallePedido>
    // DetallePedido ya NO tiene relación con Producto (es un snapshot inmutable)
}

