package com.animall.api_tienda.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.animall.api_tienda.model.Favorito;
import com.animall.api_tienda.service.FavoritoService;

@RestController
@RequestMapping("/api/usuarios/{usuarioId}/favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @GetMapping
    public List<Favorito> listarFavoritos(@PathVariable Long usuarioId) {
        return favoritoService.listarPorUsuario(usuarioId);
    }

    @PostMapping
    public ResponseEntity<Favorito> agregarAFavoritos(@PathVariable Long usuarioId,
                                                      @RequestParam Long productoId) {
        Favorito favorito = favoritoService.agregarAFavoritos(usuarioId, productoId);
        return ResponseEntity.ok(favorito);
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminarDeFavoritos(@PathVariable Long usuarioId,
                                                    @RequestParam Long productoId) {
        favoritoService.eliminarDeFavoritos(usuarioId, productoId);
        return ResponseEntity.noContent().build();
    }
}

