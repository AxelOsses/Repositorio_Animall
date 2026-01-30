package com.animall.api_tienda.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animall.api_tienda.model.CategoriaSoporte;
import com.animall.api_tienda.model.EstadoCaso;
import com.animall.api_tienda.model.EstadoPedido;
import com.animall.api_tienda.model.Rol;
import com.animall.api_tienda.repository.CategoriaSoporteRepository;
import com.animall.api_tienda.repository.EstadoCasoRepository;
import com.animall.api_tienda.repository.EstadoPedidoRepository;
import com.animall.api_tienda.repository.RolRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador de solo lectura para catálogos usados en el frontend
 * (roles al registrar usuario, estados de pedido, categorías y estados de soporte).
 */
@RestController
@RequestMapping("/api/catalogos")
@Tag(name = "Catálogos", description = "Roles, estados de pedido, categorías y estados de soporte (solo lectura)")
public class CatalogController {

    private final RolRepository rolRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final CategoriaSoporteRepository categoriaSoporteRepository;
    private final EstadoCasoRepository estadoCasoRepository;

    public CatalogController(RolRepository rolRepository,
                             EstadoPedidoRepository estadoPedidoRepository,
                             CategoriaSoporteRepository categoriaSoporteRepository,
                             EstadoCasoRepository estadoCasoRepository) {
        this.rolRepository = rolRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
        this.categoriaSoporteRepository = categoriaSoporteRepository;
        this.estadoCasoRepository = estadoCasoRepository;
    }

    @GetMapping("/roles")
    @Operation(summary = "Listar todos los roles")
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    @GetMapping("/roles/{id}")
    @Operation(summary = "Obtener rol por ID")
    public ResponseEntity<Rol> obtenerRol(@PathVariable Long id) {
        return rolRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estados-pedido")
    @Operation(summary = "Listar estados de pedido")
    public List<EstadoPedido> listarEstadosPedido() {
        return estadoPedidoRepository.findAll();
    }

    @GetMapping("/estados-pedido/{id}")
    @Operation(summary = "Obtener estado de pedido por ID")
    public ResponseEntity<EstadoPedido> obtenerEstadoPedido(@PathVariable Long id) {
        return estadoPedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categorias-soporte")
    @Operation(summary = "Listar categorías de soporte")
    public List<CategoriaSoporte> listarCategoriasSoporte() {
        return categoriaSoporteRepository.findAll();
    }

    @GetMapping("/categorias-soporte/{id}")
    @Operation(summary = "Obtener categoría de soporte por ID")
    public ResponseEntity<CategoriaSoporte> obtenerCategoriaSoporte(@PathVariable Long id) {
        return categoriaSoporteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estados-caso")
    @Operation(summary = "Listar estados de caso de soporte")
    public List<EstadoCaso> listarEstadosCaso() {
        return estadoCasoRepository.findAll();
    }

    @GetMapping("/estados-caso/{id}")
    @Operation(summary = "Obtener estado de caso por ID")
    public ResponseEntity<EstadoCaso> obtenerEstadoCaso(@PathVariable Long id) {
        return estadoCasoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
