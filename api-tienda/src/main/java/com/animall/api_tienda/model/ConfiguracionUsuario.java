package com.animall.api_tienda.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Preferencias y configuración asociada a un usuario.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "configuracion_usuario")
public class ConfiguracionUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion", updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private Boolean notificaciones = Boolean.TRUE;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String idioma = "es";

    @OneToOne(mappedBy = "configuracion")
    private Usuario usuario;
}

