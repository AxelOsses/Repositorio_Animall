package com.animall.api_tienda.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request para crear un pedido directamente (sin pasar por carrito).
 * El frontend envía solo IDs y cantidades; el backend:
 * - Valida existencia de productos, dirección, método de pago
 * - Valida stock disponible
 * - Calcula precios unitarios con descuento desde BD
 * - Calcula el total del pedido
 * - Asigna estado inicial (CREADO)
 * - Persiste el pedido completo con sus detalles
 */
@Schema(description = "Datos para crear un pedido directamente. Solo IDs y cantidades. El backend calcula todo.")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrearPedidoRequest {

    @Schema(description = "ID del usuario que realiza el pedido (en producción vendría del token de auth)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "usuarioId es obligatorio")
    private Long usuarioId;

    @Schema(description = "ID de la dirección de envío (debe pertenecer al usuario)", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "direccionId es obligatorio")
    private Long direccionId;

    @Schema(description = "ID del método de pago (debe pertenecer al usuario)", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "metodoPagoId es obligatorio")
    private Long metodoPagoId;

    @Schema(description = "Lista de productos y cantidades. El backend obtiene precios de BD y valida stock.")
    @NotEmpty(message = "Debe haber al menos un item en el pedido")
    @Valid
    private List<ItemPedidoRequest> items;
}
