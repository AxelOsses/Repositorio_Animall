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

import com.animall.api_tienda.model.MetodoPago;
import com.animall.api_tienda.service.MetodoPagoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios/{usuarioId}/metodos-pago")
@Tag(name = "Métodos de pago", description = "Métodos de pago registrados por usuario")
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;

    public MetodoPagoController(MetodoPagoService metodoPagoService) {
        this.metodoPagoService = metodoPagoService;
    }

    @GetMapping
    @Operation(summary = "Listar métodos de pago del usuario")
    public List<MetodoPago> listarPorUsuario(@PathVariable Long usuarioId) {
        return metodoPagoService.listarPorUsuario(usuarioId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener método de pago por ID")
    public ResponseEntity<MetodoPago> obtenerPorId(@PathVariable Long usuarioId, @PathVariable Long id) {
        return metodoPagoService.buscarPorId(id)
                .filter(m -> m.getUsuario().getId().equals(usuarioId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Registrar método de pago")
    public ResponseEntity<MetodoPago> crear(@PathVariable Long usuarioId, @Valid @RequestBody MetodoPago metodoPago) {
        MetodoPago creado = metodoPagoService.crear(usuarioId, metodoPago);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar método de pago")
    public ResponseEntity<MetodoPago> actualizar(@PathVariable Long usuarioId,
                                                 @PathVariable Long id,
                                                 @Valid @RequestBody MetodoPago metodoPago) {
        try {
            MetodoPago actualizado = metodoPagoService.actualizar(id, usuarioId, metodoPago);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar método de pago")
    public ResponseEntity<Void> eliminar(@PathVariable Long usuarioId, @PathVariable Long id) {
        try {
            metodoPagoService.eliminar(id, usuarioId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
