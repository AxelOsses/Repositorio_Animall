package com.animall.api_tienda.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animall.api_tienda.model.Carrito;
import com.animall.api_tienda.model.ItemCarrito;
import com.animall.api_tienda.model.Producto;
import com.animall.api_tienda.model.Usuario;
import com.animall.api_tienda.repository.CarritoRepository;
import com.animall.api_tienda.repository.ItemCarritoRepository;
import com.animall.api_tienda.repository.ProductoRepository;
import com.animall.api_tienda.repository.UsuarioRepository;
import com.animall.api_tienda.service.CarritoService;

@Service
@Transactional
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final ItemCarritoRepository itemCarritoRepository;

    public CarritoServiceImpl(CarritoRepository carritoRepository,
                              UsuarioRepository usuarioRepository,
                              ProductoRepository productoRepository,
                              ItemCarritoRepository itemCarritoRepository) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.itemCarritoRepository = itemCarritoRepository;
    }

    @Override
    public Carrito obtenerOCrearCarritoParaUsuario(Long usuarioId) {
        // Nota: NO es readOnly porque puede crear un carrito nuevo
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id " + usuarioId));

        return carritoRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUsuario(usuario);
                    return carritoRepository.save(nuevo);
                });
    }

    @Override
    public Carrito agregarProducto(Long usuarioId, Long productoId, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        Carrito carrito = obtenerOCrearCarritoParaUsuario(usuarioId);

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id " + productoId));

        if (producto.getStock() <= 0) {
            throw new IllegalStateException("El producto no tiene stock disponible");
        }

        // Buscar si ya existe un item para este producto en el carrito
        Optional<ItemCarrito> existenteOpt = carrito.getItems().stream()
                .filter(item -> item.getProducto().getId().equals(productoId))
                .findFirst();

        int nuevaCantidadSolicitada = cantidad;

        if (existenteOpt.isPresent()) {
            ItemCarrito existente = existenteOpt.get();
            nuevaCantidadSolicitada = existente.getCantidad() + cantidad;
        }

        if (nuevaCantidadSolicitada > producto.getStock()) {
            throw new IllegalStateException("La cantidad solicitada supera el stock disponible");
        }

        if (existenteOpt.isPresent()) {
            ItemCarrito existente = existenteOpt.get();
            existente.setCantidad(nuevaCantidadSolicitada);
            itemCarritoRepository.save(existente);
        } else {
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setPrecioUnitario(producto.getPrecio()); // solo referencia, el descuento se aplica al confirmar pedido
            carrito.getItems().add(nuevoItem);
        }

        return carritoRepository.save(carrito);
    }

    @Override
    public Carrito actualizarCantidadItem(Long usuarioId, Long itemCarritoId, int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        Carrito carrito = obtenerOCrearCarritoParaUsuario(usuarioId);

        ItemCarrito item = carrito.getItems().stream()
                .filter(i -> i.getId().equals(itemCarritoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item de carrito no encontrado para el usuario"));

        Producto producto = item.getProducto();
        if (nuevaCantidad > producto.getStock()) {
            throw new IllegalStateException("La cantidad solicitada supera el stock disponible");
        }

        item.setCantidad(nuevaCantidad);
        itemCarritoRepository.save(item);

        return carritoRepository.save(carrito);
    }

    @Override
    public Carrito eliminarItem(Long usuarioId, Long itemCarritoId) {
        Carrito carrito = obtenerOCrearCarritoParaUsuario(usuarioId);

        ItemCarrito item = carrito.getItems().stream()
                .filter(i -> i.getId().equals(itemCarritoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item de carrito no encontrado para el usuario"));

        carrito.getItems().remove(item);
        itemCarritoRepository.delete(item);

        return carritoRepository.save(carrito);
    }

    @Override
    public Carrito vaciarCarrito(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarritoParaUsuario(usuarioId);
        carrito.getItems().clear();
        return carritoRepository.save(carrito);
    }
}

