package com.animall.api_tienda.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animall.api_tienda.dto.ProductoCreateRequest;
import com.animall.api_tienda.dto.ProductoUpdateRequest;
import com.animall.api_tienda.model.Categoria;
import com.animall.api_tienda.model.Producto;
import com.animall.api_tienda.repository.CategoriaRepository;
import com.animall.api_tienda.repository.FavoritoRepository;
import com.animall.api_tienda.repository.ProductoRepository;
import com.animall.api_tienda.service.ProductoService;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FavoritoRepository favoritoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository,
                               CategoriaRepository categoriaRepository,
                               FavoritoRepository favoritoRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.favoritoRepository = favoritoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombre(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarPorCategoria(Long idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con id " + idCategoria));
        return productoRepository.findByCategoria(categoria);
    }

    @Override
    public Producto crear(ProductoCreateRequest request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con id " + request.getCategoriaId()));
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setPorcentajeDescuento(request.getPorcentajeDescuento() != null ? request.getPorcentajeDescuento() : 0);
        producto.setStock(request.getStock());
        producto.setImagen(request.getImagen());
        producto.setCategoria(categoria);
        return productoRepository.save(producto);
    }

    @Override
    public Producto actualizar(Long id, ProductoUpdateRequest request) {
        return productoRepository.findById(id)
                .map(actual -> {
                    if (request.getNombre() != null) actual.setNombre(request.getNombre());
                    if (request.getDescripcion() != null) actual.setDescripcion(request.getDescripcion());
                    if (request.getPrecio() != null) actual.setPrecio(request.getPrecio());
                    if (request.getPorcentajeDescuento() != null) actual.setPorcentajeDescuento(request.getPorcentajeDescuento());
                    if (request.getStock() != null) actual.setStock(request.getStock());
                    if (request.getImagen() != null) actual.setImagen(request.getImagen());
                    if (request.getCategoriaId() != null) {
                        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con id " + request.getCategoriaId()));
                        actual.setCategoria(categoria);
                    }
                    return productoRepository.save(actual);
                })
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id " + id));
    }

    @Override
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id " + id));

        // Regla: eliminar un producto elimina sus favoritos asociados.
        favoritoRepository.deleteAll(producto.getFavoritos());

        productoRepository.delete(producto);
    }
}

