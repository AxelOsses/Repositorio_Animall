package com.animall.api_tienda.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Detalle de un pedido - SNAPSHOT inmutable del producto al momento de la compra.
 * 
 * NO tiene relación @ManyToOne con Producto porque:
 * - El precio/nombre del producto puede cambiar después
 * - El pedido debe reflejar los datos exactos al momento de la compra
 * - El historial de compras debe ser inmutable
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_pedido", updatable = false, nullable = false)
    private Long id;

    /**
     * Referencia histórica al producto original (sin FK).
     * Útil para trazabilidad, pero el pedido no depende de que el producto exista.
     */
    @Column(name = "producto_id_original")
    private Long productoIdOriginal;

    /**
     * Nombre del producto al momento de la compra (snapshot).
     */
    @NotBlank
    @Size(max = 255)
    @Column(name = "nombre_producto", nullable = false, length = 255)
    private String nombreProducto;

    /**
     * Precio unitario al momento de la compra (ya con descuento aplicado).
     */
    @NotNull
    @Min(0)
    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    /**
     * Cantidad comprada.
     */
    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer cantidad;

    /**
     * Subtotal de esta línea (precioUnitario × cantidad).
     * Se calcula en el backend y se persiste para consultas históricas.
     */
    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Double subtotal;

    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    @JsonIgnore
    private Pedido pedido;

    /**
     * Calcula y establece el subtotal basado en precio y cantidad.
     */
    public void calcularSubtotal() {
        this.subtotal = this.precioUnitario * this.cantidad;
    }
}
