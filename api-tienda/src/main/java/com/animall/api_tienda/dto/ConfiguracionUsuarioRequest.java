package com.animall.api_tienda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuración de usuario que puede enviar el cliente al crear/actualizar.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionUsuarioRequest {

    private Boolean notificaciones = true;

    @NotBlank
    @Size(max = 20)
    private String idioma = "es";
}
