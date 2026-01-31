package com.animall.api_tienda.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pedido realizado por un usuario - INMUTABLE una vez creado.
 * 
 * Contiene SNAPSHOTS de:
 * - Dirección de envío (copiada al momento de la compra)
 * - Método de pago (copiado al momento de la compra)
 * - Detalles del pedido (snapshots de productos)
 * 
 * NO depende de entidades vivas (Direccion, MetodoPago, Producto) porque
 * estas pueden cambiar después de la compra y el historial debe ser inmutable.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido", updatable = false, nullable = false)
    @JsonProperty("id_pedido")
    private Long id;

    @NotNull
    @Column(name = "fecha_pedido", nullable = false)
    private LocalDate fechaPedido;

    /**
     * Total del pedido (calculado por el backend como suma de subtotales).
     */
    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Double total;

    /**
     * Usuario que realizó el pedido.
     * Esta relación SÍ se mantiene porque necesitamos saber de quién es el pedido.
     */
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    /**
     * Estado actual del pedido (CREADO, EN_PROCESO, ENVIADO, ENTREGADO, CANCELADO).
     * Esta relación SÍ se mantiene porque EstadoPedido es un catálogo inmutable.
     */
    @ManyToOne
    @JoinColumn(name = "id_estado_pedido", nullable = false)
    private EstadoPedido estado;

    // ==================== SNAPSHOT DE DIRECCIÓN ====================
    // Copiados al momento de la compra - NO dependen de la entidad Direccion

    @NotBlank
    @Size(max = 100)
    @Column(name = "direccion_region", nullable = false, length = 100)
    private String direccionRegion;

    @NotBlank
    @Size(max = 100)
    @Column(name = "direccion_comuna", nullable = false, length = 100)
    private String direccionComuna;

    @NotBlank
    @Size(max = 255)
    @Column(name = "direccion_calle", nullable = false, length = 255)
    private String direccionCalle;

    // ==================== SNAPSHOT DE MÉTODO DE PAGO ====================
    // Copiados al momento de la compra - NO dependen de la entidad MetodoPago

    @NotBlank
    @Size(max = 50)
    @Column(name = "metodo_pago_tipo", nullable = false, length = 50)
    private String metodoPagoTipo;

    @NotBlank
    @Size(max = 100)
    @Column(name = "metodo_pago_alias", nullable = false, length = 100)
    private String metodoPagoAlias;

    // ==================== DETALLES DEL PEDIDO ====================

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (fechaPedido == null) {
            fechaPedido = LocalDate.now();
        }
    }

    /**
     * Copia los datos de una Direccion al snapshot del pedido.
     */
    public void copiarDireccion(Direccion direccion) {
        this.direccionRegion = direccion.getRegion();
        this.direccionComuna = direccion.getComuna();
        this.direccionCalle = direccion.getDireccion();
    }

    /**
     * Copia los datos de un MetodoPago al snapshot del pedido.
     */
    public void copiarMetodoPago(MetodoPago metodoPago) {
        this.metodoPagoTipo = metodoPago.getTipo();
        this.metodoPagoAlias = metodoPago.getAlias();
    }

    /**
     * Agrega un detalle al pedido y lo asocia.
     */
    public void agregarDetalle(DetallePedido detalle) {
        detalles.add(detalle);
        detalle.setPedido(this);
    }
}
