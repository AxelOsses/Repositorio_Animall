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

@RestController
@RequestMapping("/api/usuarios/{usuarioId}/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public Carrito obtenerCarrito(@PathVariable Long usuarioId) {
        return carritoService.obtenerOCrearCarritoParaUsuario(usuarioId);
    }

    @PostMapping("/items")
    public ResponseEntity<Carrito> agregarProducto(@PathVariable Long usuarioId,
                                                   @RequestParam Long productoId,
                                                   @RequestParam int cantidad) {
        Carrito carrito = carritoService.agregarProducto(usuarioId, productoId, cantidad);
        return ResponseEntity.ok(carrito);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<Carrito> actualizarCantidad(@PathVariable Long usuarioId,
                                                      @PathVariable Long itemId,
                                                      @RequestParam int cantidad) {
        Carrito carrito = carritoService.actualizarCantidadItem(usuarioId, itemId, cantidad);
        return ResponseEntity.ok(carrito);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Carrito> eliminarItem(@PathVariable Long usuarioId,
                                                @PathVariable Long itemId) {
        Carrito carrito = carritoService.eliminarItem(usuarioId, itemId);
        return ResponseEntity.ok(carrito);
    }

    @DeleteMapping("/items")
    public ResponseEntity<Carrito> vaciarCarrito(@PathVariable Long usuarioId) {
        Carrito carrito = carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.ok(carrito);
    }
}

