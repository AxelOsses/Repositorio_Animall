package com.animall.api_tienda.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.animall.api_tienda.model.Direccion;
import com.animall.api_tienda.service.DireccionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios/{usuarioId}/direcciones")
@Tag(name = "Direcciones", description = "Direcciones de envío/facturación por usuario")
public class DireccionController {

    private final DireccionService direccionService;

    public DireccionController(DireccionService direccionService) {
        this.direccionService = direccionService;
    }

    @GetMapping
    @Operation(summary = "Listar direcciones del usuario")
    public List<Direccion> listarPorUsuario(@PathVariable Long usuarioId) {
        return direccionService.listarPorUsuario(usuarioId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener dirección por ID")
    public ResponseEntity<Direccion> obtenerPorId(@PathVariable Long usuarioId, @PathVariable Long id) {
        return direccionService.buscarPorId(id)
                .filter(d -> d.getUsuario().getId().equals(usuarioId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear dirección")
    public ResponseEntity<Direccion> crear(@PathVariable Long usuarioId, @Valid @RequestBody Direccion direccion) {
        Direccion creada = direccionService.crear(usuarioId, direccion);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.getId())
                .toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar dirección")
    public ResponseEntity<Direccion> actualizar(@PathVariable Long usuarioId,
                                                @PathVariable Long id,
                                                @Valid @RequestBody Direccion direccion) {
        try {
            Direccion actualizada = direccionService.actualizar(id, usuarioId, direccion);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar dirección")
    public ResponseEntity<Void> eliminar(@PathVariable Long usuarioId, @PathVariable Long id) {
        try {
            direccionService.eliminar(id, usuarioId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
