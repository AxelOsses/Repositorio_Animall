package com.animall.api_tienda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request para crear una categoría. Solo se envía nombre. El id se genera en el servidor y se devuelve en la respuesta.")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaCreateRequest {

    @Schema(description = "Nombre de la categoría", example = "Alimentos", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;
}
