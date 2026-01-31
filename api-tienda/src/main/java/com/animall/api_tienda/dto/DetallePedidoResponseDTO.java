package com.animall.api_tienda.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.animall.api_tienda.model.DetallePedido;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Línea del pedido en la respuesta.
 * Refleja el SNAPSHOT inmutable del producto al momento de la compra.
 */
@Schema(description = "Detalle de una línea del pedido (snapshot del producto)")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetallePedidoResponseDTO {

    @Schema(description = "ID original del producto (referencia histórica)", example = "5")
    private Long productoIdOriginal;

    @Schema(description = "Nombre del producto al momento de la compra", example = "Alimento Premium para Perros")
    private String nombreProducto;

    @Schema(description = "Cantidad comprada", example = "2")
    private Integer cantidad;

    @Schema(description = "Precio unitario aplicado (con descuento)", example = "15.99")
    private Double precioUnitario;

    @Schema(description = "Subtotal de esta línea (precio × cantidad)", example = "31.98")
    private Double subtotal;

    /**
     * Convierte un DetallePedido (snapshot) a DTO.
     * Los datos vienen directamente del detalle, no de una entidad Producto.
     */
    public static DetallePedidoResponseDTO from(DetallePedido detalle) {
        if (detalle == null) return null;
        DetallePedidoResponseDTO dto = new DetallePedidoResponseDTO();
        dto.setProductoIdOriginal(detalle.getProductoIdOriginal());
        dto.setNombreProducto(detalle.getNombreProducto());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getSubtotal());
        return dto;
    }

    public static List<DetallePedidoResponseDTO> fromList(List<DetallePedido> detalles) {
        if (detalles == null) return List.of();
        return detalles.stream().map(DetallePedidoResponseDTO::from).collect(Collectors.toList());
    }
}
