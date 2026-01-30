package com.animall.api_tienda.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cuerpo correcto para PUT /api/usuarios/{id}.
 * No incluye id, puntos ni ahorroTotal (reglas de negocio).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateRequest {

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @NotBlank
    @Size(min = 6, max = 255)
    private String password;

    @NotNull
    private Long rolId;

    /** URL o enlace de referencia a la imagen de perfil. Opcional. */
    @Size(max = 500)
    private String imagenPerfil;

    @Valid
    private ConfiguracionUsuarioRequest configuracion;
}
