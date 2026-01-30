package com.animall.api_tienda.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request para confirmar un pedido desde el carrito.
 * Solo se envían los IDs de dirección y método de pago.
 */
@Schema(description = "Datos mínimos para confirmar el pedido. El resto lo genera el sistema.")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoCreateDTO {

    @Schema(description = "ID de la dirección de envío", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "direccionId es obligatorio")
    private Long direccionId;

    @Schema(description = "ID del método de pago", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "metodoPagoId es obligatorio")
    private Long metodoPagoId;
}
