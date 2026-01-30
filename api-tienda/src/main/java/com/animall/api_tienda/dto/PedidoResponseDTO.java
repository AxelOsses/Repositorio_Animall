package com.animall.api_tienda.dto;

import java.time.LocalDate;
import java.util.List;

import com.animall.api_tienda.model.Pedido;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta de pedido generado por el sistema. Sin exponer entidades JPA.
 */
@Schema(description = "Pedido generado por el sistema. No enviar este objeto en el request.")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoResponseDTO {

    @Schema(description = "ID del pedido (generado por el servidor)", example = "1")
    private Long id;

    @Schema(description = "Fecha del pedido", example = "2026-01-28")
    private LocalDate fechaPedido;

    @Schema(description = "Total del pedido", example = "45.50")
    private Double total;

    @Schema(description = "Estado actual del pedido", example = "CREADO")
    private String estadoNombre;

    @Schema(description = "Resumen de la dirección de envío", example = "Metropolitana, Santiago, Av. Principal 123")
    private String direccionResumen;

    @Schema(description = "Resumen del método de pago", example = "Tarjeta - ****1234")
    private String metodoPagoResumen;

    @Schema(description = "Líneas del pedido")
    private List<DetallePedidoResponseDTO> detalles;

    public static PedidoResponseDTO from(Pedido pedido) {
        if (pedido == null) return null;
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setTotal(pedido.getTotal());
        dto.setEstadoNombre(pedido.getEstado() != null ? pedido.getEstado().getNombre() : null);
        if (pedido.getDireccion() != null) {
            var d = pedido.getDireccion();
            dto.setDireccionResumen(d.getRegion() + ", " + d.getComuna() + ", " + d.getDireccion());
        }
        if (pedido.getMetodoPago() != null) {
            var m = pedido.getMetodoPago();
            dto.setMetodoPagoResumen(m.getTipo() + " - " + m.getAlias());
        }
        dto.setDetalles(DetallePedidoResponseDTO.fromList(pedido.getDetalles()));
        return dto;
    }
}
