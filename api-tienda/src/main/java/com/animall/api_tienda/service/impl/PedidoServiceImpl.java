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
import com.animall.api_tienda.repository.DireccionRepository;
import com.animall.api_tienda.repository.EstadoPedidoRepository;
import com.animall.api_tienda.repository.MetodoPagoRepository;
import com.animall.api_tienda.repository.PedidoRepository;
import com.animall.api_tienda.repository.ProductoRepository;
import com.animall.api_tienda.repository.UsuarioRepository;
import com.animall.api_tienda.service.CarritoService;
import com.animall.api_tienda.service.PedidoService;

/**
 * Servicio para gestión de pedidos.
 * 
 * El pedido es INMUTABLE una vez creado:
 * - Contiene snapshots de dirección, método de pago y productos
 * - No depende de entidades vivas que puedan cambiar
 * 
 * PRINCIPIO: Este servicio delega la lógica del carrito a CarritoService,
 * respetando Single Responsibility y evitando acceso directo a CarritoRepository.
 */
@Service
@Transactional
public class PedidoServiceImpl implements PedidoService {

    private static final String ESTADO_INICIAL = "CREADO";

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final ProductoRepository productoRepository;
    private final CarritoService carritoService;  // ← Servicio, NO repositorio

    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                             UsuarioRepository usuarioRepository,
                             DireccionRepository direccionRepository,
                             MetodoPagoRepository metodoPagoRepository,
                             EstadoPedidoRepository estadoPedidoRepository,
                             ProductoRepository productoRepository,
                             CarritoService carritoService) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.direccionRepository = direccionRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
        this.productoRepository = productoRepository;
        this.carritoService = carritoService;
    }

    /**
     * Confirma un pedido tomando los items del carrito activo del usuario.
     * 
     * El frontend envía solo direccionId y metodoPagoId.
     * El backend:
     * 1. Obtiene el carrito del usuario (delegando a CarritoService)
     * 2. Valida que no esté vacío
     * 3. COPIA los datos de dirección al pedido (snapshot)
     * 4. COPIA los datos de método de pago al pedido (snapshot)
     * 5. Convierte cada ItemCarrito en DetallePedido (snapshot del producto)
     * 6. Calcula el total como suma de subtotales
     * 7. Asigna estado inicial (CREADO)
     * 8. Descuenta stock de productos
     * 9. Vacía el carrito (delegando a CarritoService)
     * 10. Actualiza puntos y ahorro del usuario
     */
    @Override
    public Pedido confirmarPedidoDesdeCarrito(Long usuarioId, Long direccionId, Long metodoPagoId) {
        // 1. Validar usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id " + usuarioId));

        // 2. Obtener carrito delegando a CarritoService (crea si no existe)
        Carrito carrito = carritoService.obtenerOCrearCarritoParaUsuario(usuarioId);

        // 3. Validar que el carrito no esté vacío
        if (carrito.getItems().isEmpty()) {
            throw new IllegalStateException("El carrito está vacío, no se puede confirmar el pedido");
        }

        // 4. Validar dirección (debe pertenecer al usuario)
        Direccion direccion = direccionRepository.findById(direccionId)
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada con id " + direccionId));

        if (!direccion.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("La dirección no pertenece al usuario");
        }

        // 5. Validar método de pago (debe pertenecer al usuario)
        MetodoPago metodoPago = metodoPagoRepository.findById(metodoPagoId)
                .orElseThrow(() -> new IllegalArgumentException("Método de pago no encontrado con id " + metodoPagoId));

        if (!metodoPago.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("El método de pago no pertenece al usuario");
        }

        // 6. Obtener estado inicial
        EstadoPedido estadoInicial = estadoPedidoRepository.findByNombre(ESTADO_INICIAL)
                .orElseThrow(() -> new IllegalStateException("No se encontró el estado de pedido inicial: " + ESTADO_INICIAL));

        // 7. Crear pedido con snapshots
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEstado(estadoInicial);
        
        // SNAPSHOT de dirección (copiamos los valores, no la referencia)
        pedido.copiarDireccion(direccion);
        
        // SNAPSHOT de método de pago (copiamos los valores, no la referencia)
        pedido.copiarMetodoPago(metodoPago);

        // 8. Convertir ItemCarrito → DetallePedido (snapshots de productos)
        double total = 0.0;
        double ahorroTotalPedido = 0.0;

        for (ItemCarrito item : carrito.getItems()) {
            Producto producto = item.getProducto();
            int cantidad = item.getCantidad();

            // Validaciones
            if (cantidad <= 0) {
                throw new IllegalStateException("Cantidad inválida en el carrito para el producto " + producto.getId());
            }

            if (producto.getStock() < cantidad) {
                throw new IllegalStateException("Stock insuficiente para el producto '" + producto.getNombre() 
                        + "' (id=" + producto.getId() + "). Stock disponible: " + producto.getStock() 
                        + ", cantidad solicitada: " + cantidad);
            }

            // Calcular precio con descuento
            double precioBase = producto.getPrecio();
            int porcentajeDescuento = producto.getPorcentajeDescuento() != null ? producto.getPorcentajeDescuento() : 0;
            double descuentoUnitario = precioBase * porcentajeDescuento / 100.0;
            double precioFinalUnitario = precioBase - descuentoUnitario;
            double subtotal = precioFinalUnitario * cantidad;

            // Crear DetallePedido como SNAPSHOT (sin relación con Producto)
            DetallePedido detalle = new DetallePedido();
            detalle.setProductoIdOriginal(producto.getId());  // Solo referencia histórica
            detalle.setNombreProducto(producto.getNombre());   // Snapshot del nombre
            detalle.setPrecioUnitario(precioFinalUnitario);    // Snapshot del precio
            detalle.setCantidad(cantidad);
            detalle.setSubtotal(subtotal);

            // Agregar al pedido (cascade se encarga de persistir)
            pedido.agregarDetalle(detalle);

            // Acumular totales
            total += subtotal;
            ahorroTotalPedido += descuentoUnitario * cantidad;

            // Descontar stock del producto
            producto.setStock(producto.getStock() - cantidad);
        }

        // 9. Establecer total calculado
        pedido.setTotal(total);

        // 10. Guardar pedido (cascade guarda los detalles automáticamente)
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // 11. Actualizar puntos y ahorro del usuario
        int puntosGanados = (int) Math.floor(total);
        usuario.setPuntos(usuario.getPuntos() + puntosGanados);
        usuario.setAhorroTotal(usuario.getAhorroTotal() + ahorroTotalPedido);

        // 12. Vaciar carrito delegando a CarritoService
        carritoService.vaciarCarrito(usuarioId);

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

    /**
     * Cambia el estado de un pedido.
     * 
     * Si se cancela antes de entrega, se restituye el stock usando productoIdOriginal
     * del snapshot para buscar el producto actual.
     */
    @Override
    public Pedido cambiarEstado(Long pedidoId, String nombreNuevoEstado, boolean esAdmin) {
        Pedido pedido = pedidoRepository.findByIdWithDetalles(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con id " + pedidoId));

        EstadoPedido estadoActual = pedido.getEstado();

        // Validar que no se modifique un pedido entregado
        if ("ENTREGADO".equalsIgnoreCase(estadoActual.getNombre())
                && !estadoActual.getNombre().equalsIgnoreCase(nombreNuevoEstado)) {
            throw new IllegalStateException("Un pedido entregado no puede volver a estados anteriores");
        }

        // Solo admin puede cambiar estados (excepto cancelar)
        if (!esAdmin && !"CANCELADO".equalsIgnoreCase(nombreNuevoEstado)) {
            throw new IllegalStateException("Solo un administrador puede cambiar el estado del pedido");
        }

        EstadoPedido nuevoEstado = estadoPedidoRepository.findByNombre(nombreNuevoEstado)
                .orElseThrow(() -> new IllegalArgumentException("Estado de pedido no encontrado: " + nombreNuevoEstado));

        // Si se cancela antes de estar entregado, restituir stock
        if ("CANCELADO".equalsIgnoreCase(nombreNuevoEstado)
                && !"ENTREGADO".equalsIgnoreCase(estadoActual.getNombre())) {
            
            for (DetallePedido detalle : pedido.getDetalles()) {
                // Buscar producto por ID original del snapshot
                Long productoId = detalle.getProductoIdOriginal();
                if (productoId != null) {
                    productoRepository.findById(productoId).ifPresent(producto -> {
                        producto.setStock(producto.getStock() + detalle.getCantidad());
                    });
                }
            }
        }

        pedido.setEstado(nuevoEstado);
        return pedido;
    }
}
