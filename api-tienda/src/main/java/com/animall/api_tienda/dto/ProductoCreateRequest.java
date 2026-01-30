package com.animall.api_tienda.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoCreateRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String nombre;

    @Size(max = 500)
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Min(0)
    private Double precio;

    @NotNull
    @Min(0)
    private Integer porcentajeDescuento = 0;

    @NotNull(message = "El stock es obligatorio")
    @Min(0)
    private Integer stock;

    @Size(max = 500)
    private String imagen;

    @NotNull(message = "La categoría (categoriaId) es obligatoria")
    private Long categoriaId;
}
