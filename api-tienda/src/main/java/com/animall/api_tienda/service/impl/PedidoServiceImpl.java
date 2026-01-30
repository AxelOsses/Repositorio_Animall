package com.animall.api_tienda.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animall.api_tienda.model.Carrito;
import com.animall.api_tienda.model.DetallePedido;
import com.animall.api_tienda.model.Direccion;
import com.animall.api_tienda.model.EstadoPedido;
import com.animall.api_tienda.model.ItemCarrito;
import com.animall.api_tienda.model.MetodoPago;
import com.animall.api_tienda.model.Pedido;
import com.animall.api_tienda.model.Producto;
import com.animall.api_tienda.model.Usuario;
import com.animall.api_tienda.repository.CarritoRepository;
import com.animall.api_tienda.repository.DetallePedidoRepository;
import com.animall.api_tienda.repository.DireccionRepository;
import com.animall.api_tienda.repository.EstadoPedidoRepository;
import com.animall.api_tienda.repository.MetodoPagoRepository;
import com.animall.api_tienda.repository.PedidoRepository;
import com.animall.api_tienda.repository.UsuarioRepository;
import com.animall.api_tienda.service.PedidoService;

@Service
@Transactional
public class PedidoServiceImpl implements PedidoService {

    private static final String ESTADO_INICIAL = "CREADO";

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarritoRepository carritoRepository;
    private final DireccionRepository direccionRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                             UsuarioRepository usuarioRepository,
                             CarritoRepository carritoRepository,
                             DireccionRepository direccionRepository,
                             MetodoPagoRepository metodoPagoRepository,
                             EstadoPedidoRepository estadoPedidoRepository,
                             DetallePedidoRepository detallePedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.carritoRepository = carritoRepository;
        this.direccionRepository = direccionRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Override
    public Pedido confirmarPedidoDesdeCarrito(Long usuarioId, Long direccionId, Long metodoPagoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id " + usuarioId));

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalStateException("El usuario no tiene carrito activo"));

        if (carrito.getItems().isEmpty()) {
            throw new IllegalStateException("El carrito está vacío, no se puede confirmar el pedido");
        }

        Direccion direccion = direccionRepository.findById(direccionId)
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada con id " + direccionId));

        if (!direccion.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("La dirección no pertenece al usuario");
        }

        MetodoPago metodoPago = metodoPagoRepository.findById(metodoPagoId)
                .orElseThrow(() -> new IllegalArgumentException("Método de pago no encontrado con id " + metodoPagoId));

        if (!metodoPago.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("El método de pago no pertenece al usuario");
        }

        EstadoPedido estadoInicial = estadoPedidoRepository.findByNombre(ESTADO_INICIAL)
                .orElseThrow(() -> new IllegalStateException("No se encontró el estado de pedido inicial: " + ESTADO_INICIAL));

        // Validar stock y calcular totales
        double total = 0.0;
        double ahorroTotalPedido = 0.0;

        for (ItemCarrito item : carrito.getItems()) {
            Producto producto = item.getProducto();
            int cantidad = item.getCantidad();

            if (cantidad <= 0) {
                throw new IllegalStateException("Cantidad inválida en el carrito para el producto " + producto.getId());
            }

            if (producto.getStock() < cantidad) {
                throw new IllegalStateException("Stock insuficiente para el producto " + producto.getId());
            }

            double precioBase = producto.getPrecio();
            int porcentajeDescuento = producto.getPorcentajeDescuento() != null ? producto.getPorcentajeDescuento() : 0;
            double descuentoUnitario = precioBase * porcentajeDescuento / 100.0;
            double precioFinalUnitario = precioBase - descuentoUnitario;

            total += precioFinalUnitario * cantidad;
            ahorroTotalPedido += descuentoUnitario * cantidad;
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setDireccion(direccion);
        pedido.setMetodoPago(metodoPago);
        pedido.setEstado(estadoInicial);
        pedido.setTotal(total);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Crear detalles, descontar stock
        for (ItemCarrito item : carrito.getItems()) {
            Producto producto = item.getProducto();
            int cantidad = item.getCantidad();

            double precioBase = producto.getPrecio();
            int porcentajeDescuento = producto.getPorcentajeDescuento() != null ? producto.getPorcentajeDescuento() : 0;
            double descuentoUnitario = precioBase * porcentajeDescuento / 100.0;
            double precioFinalUnitario = precioBase - descuentoUnitario;

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedidoGuardado);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioFinalUnitario);
            detallePedidoRepository.save(detalle);
            pedidoGuardado.getDetalles().add(detalle);

            producto.setStock(producto.getStock() - cantidad);
        }

        // Actualizar puntos y ahorro del usuario
        int puntosGanados = (int) Math.floor(total);
        usuario.setPuntos(usuario.getPuntos() + puntosGanados);
        usuario.setAhorroTotal(usuario.getAhorroTotal() + ahorroTotalPedido);

        // Vaciar carrito
        carrito.getItems().clear();

        return pedidoGuardado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id " + usuarioId));
        return pedidoRepository.findByUsuarioWithDetalles(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findByIdWithDetalles(id);
    }

    @Override
    public Pedido cambiarEstado(Long pedidoId, String nombreNuevoEstado, boolean esAdmin) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con id " + pedidoId));

        EstadoPedido estadoActual = pedido.getEstado();

        if ("ENTREGADO".equalsIgnoreCase(estadoActual.getNombre())
                && !estadoActual.getNombre().equalsIgnoreCase(nombreNuevoEstado)) {
            throw new IllegalStateException("Un pedido entregado no puede volver a estados anteriores");
        }

        if (!esAdmin && !"CANCELADO".equalsIgnoreCase(nombreNuevoEstado)) {
            throw new IllegalStateException("Solo un administrador puede cambiar el estado del pedido");
        }

        EstadoPedido nuevoEstado = estadoPedidoRepository.findByNombre(nombreNuevoEstado)
                .orElseThrow(() -> new IllegalArgumentException("Estado de pedido no encontrado: " + nombreNuevoEstado));

        // Si se cancela antes de estar entregado, opcionalmente se puede restituir el stock.
        if ("CANCELADO".equalsIgnoreCase(nombreNuevoEstado)
                && !"ENTREGADO".equalsIgnoreCase(estadoActual.getNombre())) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
            }
        }

        pedido.setEstado(nuevoEstado);
        return pedidoRepository.findByIdWithDetalles(pedido.getId()).orElse(pedido);
    }
}

