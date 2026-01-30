package com.animall.api_tienda.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animall.api_tienda.dto.CategoriaCreateRequest;
import com.animall.api_tienda.dto.CategoriaUpdateRequest;
import com.animall.api_tienda.model.Categoria;
import com.animall.api_tienda.repository.CategoriaRepository;
import com.animall.api_tienda.service.CategoriaService;

@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> buscarPorNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre);
    }

    @Override
    public Categoria crear(CategoriaCreateRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        return categoriaRepository.save(categoria);
    }

    @Override
    public Categoria actualizar(Long id, CategoriaUpdateRequest request) {
        return categoriaRepository.findById(id)
                .map(actual -> {
                    if (request.getNombre() != null) actual.setNombre(request.getNombre());
                    return categoriaRepository.save(actual);
                })
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con id " + id));
    }

    @Override
    public void eliminar(Long id) {
        categoriaRepository.deleteById(id);
    }
}

