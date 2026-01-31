package com.animall.api_tienda.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.animall.api_tienda.dto.CrearPedidoRequest;
import com.animall.api_tienda.dto.PedidoCreateDTO;
import com.animall.api_tienda.dto.PedidoResponseDTO;
import com.animall.api_tienda.model.Pedido;
import com.animall.api_tienda.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Tag(name = "Pedidos", description = "Pedidos por usuario y cambio de estado")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /**
     * Endpoint principal para crear un pedido directamente (sin usar carrito).
     * El frontend envía solo IDs y cantidades; el backend calcula todo.
     * 
     * Request esperada:
     * {
     *   "usuarioId": 1,
     *   "direccionId": 3,
     *   "metodoPagoId": 2,
     *   "items": [
     *     { "productoId": 5, "cantidad": 2 },
     *     { "productoId": 8, "cantidad": 1 }
     *   ]
     * }
     */
    @PostMapping("/pedidos")
    @Operation(summary = "Crear pedido directo",
            description = "El frontend envía usuarioId, direccionId, metodoPagoId e items (productoId + cantidad). " +
                    "El backend valida stock, calcula precios con descuento, asigna estado inicial (CREADO) y persiste todo.")
    public ResponseEntity<PedidoResponseDTO> crearPedidoDirecto(@Valid @RequestBody CrearPedidoRequest request) {
        Pedido pedido = pedidoService.crearPedidoDirecto(request);
        PedidoResponseDTO response = PedidoResponseDTO.from(pedido);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/pedidos/{id}")
                .buildAndExpand(pedido.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Confirma un pedido tomando los items del carrito del usuario.
     * Útil cuando el usuario ya agregó productos al carrito.
     */
    @PostMapping("/usuarios/{usuarioId}/pedidos")
    @Operation(summary = "Confirmar pedido desde carrito",
            description = "Request: solo direccionId y metodoPagoId. Response: pedido generado por el sistema (id, total, estado, detalles, etc.).")
    public ResponseEntity<PedidoResponseDTO> confirmarPedidoDesdeCarrito(@PathVariable Long usuarioId,
                                                                          @Valid @RequestBody PedidoCreateDTO request) {
        Pedido pedido = pedidoService.confirmarPedidoDesdeCarrito(usuarioId, request.getDireccionId(), request.getMetodoPagoId());
        PedidoResponseDTO response = PedidoResponseDTO.from(pedido);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(pedido.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/usuarios/{usuarioId}/pedidos")
    @Operation(summary = "Listar pedidos del usuario")
    public List<PedidoResponseDTO> listarPedidosPorUsuario(@PathVariable Long usuarioId) {
        return pedidoService.listarPorUsuario(usuarioId).stream()
                .map(PedidoResponseDTO::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/pedidos/{pedidoId}")
    @Operation(summary = "Obtener pedido por ID")
    public ResponseEntity<PedidoResponseDTO> obtenerPedidoPorId(@PathVariable Long pedidoId) {
        return pedidoService.buscarPorId(pedidoId)
                .map(PedidoResponseDTO::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint administrativo para cambiar el estado de un pedido.
     * En este modelo didáctico se pasa un flag esAdmin por parámetro, pero
     * en un sistema real esto debería venir del contexto de seguridad.
     */
    @PutMapping("/pedidos/{pedidoId}/estado")
    @Operation(summary = "Cambiar estado del pedido (admin)")
    public ResponseEntity<PedidoResponseDTO> cambiarEstado(@PathVariable Long pedidoId,
                                                            @RequestParam String nuevoEstado,
                                                            @RequestParam(defaultValue = "false") boolean esAdmin) {
        try {
            Pedido actualizado = pedidoService.cambiarEstado(pedidoId, nuevoEstado, esAdmin);
            return ResponseEntity.ok(PedidoResponseDTO.from(actualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

