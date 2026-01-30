package com.animall.api_tienda.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.animall.api_tienda.model.CasoSoporte;
import com.animall.api_tienda.service.CasoSoporteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Casos de soporte", description = "Casos de soporte por usuario")
public class CasoSoporteController {

    private final CasoSoporteService casoSoporteService;

    public CasoSoporteController(CasoSoporteService casoSoporteService) {
        this.casoSoporteService = casoSoporteService;
    }

    @PostMapping("/usuarios/{usuarioId}/casos-soporte")
    @Operation(summary = "Crear caso de soporte")
    public ResponseEntity<CasoSoporte> crearCaso(@PathVariable Long usuarioId,
                                                 @RequestParam Long categoriaSoporteId,
                                                 @RequestParam String descripcion) {
        CasoSoporte creado = casoSoporteService.crearCaso(usuarioId, categoriaSoporteId, descripcion);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @GetMapping("/usuarios/{usuarioId}/casos-soporte")
    @Operation(summary = "Listar casos del usuario")
    public List<CasoSoporte> listarCasosPorUsuario(@PathVariable Long usuarioId) {
        return casoSoporteService.listarPorUsuario(usuarioId);
    }

    @GetMapping("/casos-soporte/{casoId}")
    @Operation(summary = "Obtener caso por ID")
    public ResponseEntity<CasoSoporte> obtenerCasoPorId(@PathVariable Long casoId) {
        return casoSoporteService.buscarPorId(casoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint administrativo para cambiar el estado de un caso de soporte.
     * De forma didáctica se recibe esAdmin por parámetro.
     */
    @PutMapping("/casos-soporte/{casoId}/estado")
    @Operation(summary = "Cambiar estado del caso (admin)")
    public ResponseEntity<CasoSoporte> cambiarEstado(@PathVariable Long casoId,
                                                     @RequestParam String nuevoEstado,
                                                     @RequestParam(defaultValue = "false") boolean esAdmin) {
        try {
            CasoSoporte actualizado = casoSoporteService.cambiarEstado(casoId, nuevoEstado, esAdmin);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

