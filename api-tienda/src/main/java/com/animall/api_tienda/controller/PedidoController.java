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

import com.animall.api_tienda.model.Pedido;
import com.animall.api_tienda.service.PedidoService;

@RestController
@RequestMapping("/api")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/usuarios/{usuarioId}/pedidos")
    public ResponseEntity<Pedido> confirmarPedidoDesdeCarrito(@PathVariable Long usuarioId,
                                                              @RequestParam Long direccionId,
                                                              @RequestParam Long metodoPagoId) {
        Pedido pedido = pedidoService.confirmarPedidoDesdeCarrito(usuarioId, direccionId, metodoPagoId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(pedido.getId())
                .toUri();
        return ResponseEntity.created(location).body(pedido);
    }

    @GetMapping("/usuarios/{usuarioId}/pedidos")
    public List<Pedido> listarPedidosPorUsuario(@PathVariable Long usuarioId) {
        return pedidoService.listarPorUsuario(usuarioId);
    }

    @GetMapping("/pedidos/{pedidoId}")
    public ResponseEntity<Pedido> obtenerPedidoPorId(@PathVariable Long pedidoId) {
        return pedidoService.buscarPorId(pedidoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint administrativo para cambiar el estado de un pedido.
     * En este modelo didáctico se pasa un flag esAdmin por parámetro, pero
     * en un sistema real esto debería venir del contexto de seguridad.
     */
    @PutMapping("/pedidos/{pedidoId}/estado")
    public ResponseEntity<Pedido> cambiarEstado(@PathVariable Long pedidoId,
                                                @RequestParam String nuevoEstado,
                                                @RequestParam(defaultValue = "false") boolean esAdmin) {
        try {
            Pedido actualizado = pedidoService.cambiarEstado(pedidoId, nuevoEstado, esAdmin);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

