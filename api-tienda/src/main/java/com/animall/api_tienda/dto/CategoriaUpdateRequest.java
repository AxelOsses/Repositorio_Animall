package com.animall.api_tienda.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaUpdateRequest {

    @Size(max = 100)
    private String nombre;
}
