package com.animall.api_tienda.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoUpdateRequest {

    @Size(max = 150)
    private String nombre;

    @Size(max = 500)
    private String descripcion;

    @Min(0)
    private Double precio;

    @Min(0)
    private Integer porcentajeDescuento;

    @Min(0)
    private Integer stock;

    @Size(max = 500)
    private String imagen;

    private Long categoriaId;
}
