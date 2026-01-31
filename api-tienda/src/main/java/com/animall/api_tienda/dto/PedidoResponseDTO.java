package com.animall.api_tienda.dto;

import java.time.LocalDate;
import java.util.List;

import com.animall.api_tienda.model.Pedido;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta de pedido generado por el sistema.
 * Contiene SNAPSHOTS inmutables - no expone entidades JPA.
 */
@Schema(description = "Pedido generado por el sistema. Datos inmutables al momento de la compra.")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoResponseDTO {

    @Schema(description = "ID del pedido (generado por el servidor)", example = "1")
    private Long id;

    @Schema(description = "Fecha del pedido", example = "2026-01-28")
    private LocalDate fechaPedido;

    @Schema(description = "Total del pedido (calculado por el backend)", example = "45.50")
    private Double total;

    @Schema(description = "Estado actual del pedido", example = "CREADO")
    private String estado;

    // ==================== SNAPSHOT DE DIRECCIÓN ====================

    @Schema(description = "Región de envío (snapshot)", example = "Metropolitana")
    private String direccionRegion;

    @Schema(description = "Comuna de envío (snapshot)", example = "Santiago")
    private String direccionComuna;

    @Schema(description = "Calle de envío (snapshot)", example = "Av. Principal 123")
    private String direccionCalle;

    // ==================== SNAPSHOT DE MÉTODO DE PAGO ====================

    @Schema(description = "Tipo de método de pago (snapshot)", example = "Tarjeta")
    private String metodoPagoTipo;

    @Schema(description = "Alias del método de pago (snapshot)", example = "****1234")
    private String metodoPagoAlias;

    // ==================== DETALLES ====================

    @Schema(description = "Líneas del pedido (snapshots de productos)")
    private List<DetallePedidoResponseDTO> detalles;

    /**
     * Convierte un Pedido a DTO.
     * Los datos de dirección y método de pago vienen del snapshot, no de entidades.
     */
    public static PedidoResponseDTO from(Pedido pedido) {
        if (pedido == null) return null;
        
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setTotal(pedido.getTotal());
        dto.setEstado(pedido.getEstado() != null ? pedido.getEstado().getNombre() : null);
        
        // Snapshot de dirección (copiado al momento de la compra)
        dto.setDireccionRegion(pedido.getDireccionRegion());
        dto.setDireccionComuna(pedido.getDireccionComuna());
        dto.setDireccionCalle(pedido.getDireccionCalle());
        
        // Snapshot de método de pago (copiado al momento de la compra)
        dto.setMetodoPagoTipo(pedido.getMetodoPagoTipo());
        dto.setMetodoPagoAlias(pedido.getMetodoPagoAlias());
        
        // Detalles (snapshots de productos)
        dto.setDetalles(DetallePedidoResponseDTO.fromList(pedido.getDetalles()));
        
        return dto;
    }
}
