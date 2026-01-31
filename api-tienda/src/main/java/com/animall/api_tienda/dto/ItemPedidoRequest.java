package com.animall.api_tienda.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representa una línea del pedido enviada por el frontend.
 * Solo contiene productoId y cantidad; el backend obtiene el precio de BD.
 */
@Schema(description = "Línea del pedido: solo productoId y cantidad. El backend calcula el precio.")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemPedidoRequest {

    @Schema(description = "ID del producto a agregar", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "productoId es obligatorio")
    private Long productoId;

    @Schema(description = "Cantidad del producto", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
}
