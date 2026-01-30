package com.animall.api_tienda.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.animall.api_tienda.model.DetallePedido;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Línea del pedido en la respuesta. Sin entidades JPA anidadas.
 */
@Schema(description = "Detalle de una línea del pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetallePedidoResponseDTO {

    @Schema(description = "ID del producto", example = "1")
    private Long productoId;

    @Schema(description = "Nombre del producto", example = "Alimento Premium")
    private String nombreProducto;

    @Schema(description = "Cantidad", example = "2")
    private Integer cantidad;

    @Schema(description = "Precio unitario aplicado", example = "15.99")
    private Double precioUnitario;

    public static DetallePedidoResponseDTO from(DetallePedido detalle) {
        if (detalle == null) return null;
        DetallePedidoResponseDTO dto = new DetallePedidoResponseDTO();
        dto.setProductoId(detalle.getProducto() != null ? detalle.getProducto().getId() : null);
        dto.setNombreProducto(detalle.getProducto() != null ? detalle.getProducto().getNombre() : null);
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        return dto;
    }

    public static List<DetallePedidoResponseDTO> fromList(List<DetallePedido> detalles) {
        if (detalles == null) return List.of();
        return detalles.stream().map(DetallePedidoResponseDTO::from).collect(Collectors.toList());
    }
}
