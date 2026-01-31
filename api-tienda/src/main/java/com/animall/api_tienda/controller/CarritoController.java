package com.animall.api_tienda.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.animall.api_tienda.model.Carrito;
import com.animall.api_tienda.service.CarritoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST API para los ÍTEMS del carrito.
 *
 * El carrito es un agregado raíz implícito (uno por usuario) que se crea
 * automáticamente cuando se agrega el primer producto.
 *
 * ❌ NO existe POST ni DELETE sobre /carrito (el carrito no es un recurso CRUD).
 * ✅ La interacción del usuario es exclusivamente mediante /carrito/items.
 * ✅ El carrito se obtiene o crea de forma implícita en el backend.
 */
@RestController
@RequestMapping("/api/usuarios/{usuarioId}/carrito")
@Tag(name = "Carrito - Items", description = "Gestión de ítems del carrito. El carrito se crea implícitamente al agregar el primer producto.")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    /**
     * Obtiene los ítems del carrito del usuario.
     * Crea el carrito implícitamente si no existe (devuelve lista vacía).
     */
    @GetMapping("/items")
    @Operation(summary = "Listar ítems del carrito",
            description = "Obtiene el carrito con sus ítems. El carrito se crea implícitamente si no existe.")
    public ResponseEntity<Carrito> listarItems(@PathVariable Long usuarioId) {
        Carrito carrito = carritoService.obtenerOCrearCarritoParaUsuario(usuarioId);
        return ResponseEntity.ok(carrito);
    }

    /**
     * Agrega un producto al carrito (o incrementa cantidad si ya existe).
     * Crea el carrito implícitamente si es el primer producto.
     */
    @PostMapping("/items")
    @Operation(summary = "Agregar producto al carrito",
            description = "Agrega producto por ID y cantidad. Crea el carrito si no existe.")
    public ResponseEntity<Carrito> agregarProducto(@PathVariable Long usuarioId,
                                                   @RequestParam Long productoId,
                                                   @RequestParam int cantidad) {
        Carrito carrito = carritoService.agregarProducto(usuarioId, productoId, cantidad);
        return ResponseEntity.ok(carrito);
    }

    /**
     * Actualiza la cantidad de un ítem existente.
     */
    @PutMapping("/items/{itemId}")
    @Operation(summary = "Actualizar cantidad de ítem")
    public ResponseEntity<Carrito> actualizarCantidad(@PathVariable Long usuarioId,
                                                      @PathVariable Long itemId,
                                                      @RequestParam int cantidad) {
        Carrito carrito = carritoService.actualizarCantidadItem(usuarioId, itemId, cantidad);
        return ResponseEntity.ok(carrito);
    }

    /**
     * Elimina un ítem del carrito.
     */
    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Eliminar ítem del carrito")
    public ResponseEntity<Carrito> eliminarItem(@PathVariable Long usuarioId,
                                                @PathVariable Long itemId) {
        Carrito carrito = carritoService.eliminarItem(usuarioId, itemId);
        return ResponseEntity.ok(carrito);
    }

    /**
     * Vacía el carrito (elimina todos los ítems).
     * El carrito permanece; solo se limpian sus ítems.
     */
    @DeleteMapping("/items")
    @Operation(summary = "Vaciar carrito",
            description = "Elimina todos los ítems del carrito. El carrito permanece vacío.")
    public ResponseEntity<Carrito> vaciarCarrito(@PathVariable Long usuarioId) {
        Carrito carrito = carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.ok(carrito);
    }
}
