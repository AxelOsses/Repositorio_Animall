package com.animall.api_tienda.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Caso de soporte creado por un usuario.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "caso_soporte")
public class CasoSoporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caso_soporte", updatable = false, nullable = false)
    private Long id;

    @NotBlank
    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String descripcion;

    @NotNull
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_categoria_soporte", nullable = false)
    private CategoriaSoporte categoriaSoporte;

    @ManyToOne
    @JoinColumn(name = "id_estado_caso", nullable = false)
    private EstadoCaso estadoCaso;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDate.now();
        }
    }
}

