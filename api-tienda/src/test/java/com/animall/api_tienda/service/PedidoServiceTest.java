package com.animall.api_tienda.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

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
import com.animall.api_tienda.service.impl.PedidoServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de la lógica de negocio de PedidoServiceImpl.
 *
 * Enfoque:
 * - No se levanta contexto de Spring (@SpringBootTest).
 * - Se mockean todas las dependencias (repositorios y CarritoService).
 * - Cada test prueba UNA sola regla de negocio.
 */
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private DireccionRepository direccionRepository;
    @Mock
    private MetodoPagoRepository metodoPagoRepository;
    @Mock
    private EstadoPedidoRepository estadoPedidoRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private CarritoService carritoService;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private static final Long USUARIO_ID = 1L;
    private static final Long DIRECCION_ID = 10L;
    private static final Long METODO_PAGO_ID = 20L;

    // ============================================================
    // 1) Cálculo del total del pedido desde los ítems del carrito
    // ============================================================
    @Test
    @DisplayName("confirmarPedidoDesdeCarrito calcula el total solo desde los ítems del carrito (backend)")
    void confirmarPedidoDesdeCarrito_calculaTotalSoloDesdeItemsCarrito() {
        // GIVEN
        Usuario usuario = new Usuario();
        usuario.setId(USUARIO_ID);
        usuario.setPuntos(0);
        usuario.setAhorroTotal(0.0);

        Direccion direccion = new Direccion();
        direccion.setId(DIRECCION_ID);
        direccion.setUsuario(usuario);

        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setId(METODO_PAGO_ID);
        metodoPago.setUsuario(usuario);

        EstadoPedido estadoCreado = new EstadoPedido();
        estadoCreado.setNombre("CREADO");

        Producto producto1 = new Producto();
        producto1.setId(100L);
        producto1.setNombre("Prod1");
        producto1.setPrecio(10.0);          // base
        producto1.setPorcentajeDescuento(20); // 20% desc → 8.0
        producto1.setStock(10);

        Producto producto2 = new Producto();
        producto2.setId(200L);
        producto2.setNombre("Prod2");
        producto2.setPrecio(5.0);           // sin descuento
        producto2.setPorcentajeDescuento(0);
        producto2.setStock(5);

        ItemCarrito item1 = new ItemCarrito();
        item1.setProducto(producto1);
        item1.setCantidad(2);               // 2 * 8.0 = 16.0

        ItemCarrito item2 = new ItemCarrito();
        item2.setProducto(producto2);
        item2.setCantidad(3);               // 3 * 5.0 = 15.0

        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setItems(List.of(item1, item2));

        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(carritoService.obtenerOCrearCarritoParaUsuario(USUARIO_ID)).thenReturn(carrito);
        when(direccionRepository.findById(DIRECCION_ID)).thenReturn(Optional.of(direccion));
        when(metodoPagoRepository.findById(METODO_PAGO_ID)).thenReturn(Optional.of(metodoPago));
        when(estadoPedidoRepository.findByNombre("CREADO")).thenReturn(Optional.of(estadoCreado));

        // Capturamos el Pedido persistido para verificar el total calculado
        ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
        when(pedidoRepository.save(pedidoCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        Pedido pedidoResultado = pedidoService.confirmarPedidoDesdeCarrito(
                USUARIO_ID, DIRECCION_ID, METODO_PAGO_ID);

        // THEN
        // Total esperado (solo desde items del carrito, calculado en backend):
        // producto1: 10 con 20% desc → 8.0 * 2 = 16.0
        // producto2: 5 sin desc      → 5.0 * 3 = 15.0
        double totalEsperado = 16.0 + 15.0;

        Pedido pedidoGuardado = pedidoCaptor.getValue();
        assertEquals(totalEsperado, pedidoGuardado.getTotal(), 0.0001);
        assertEquals(totalEsperado, pedidoResultado.getTotal(), 0.0001);

        // No existe ningún parámetro de total proveniente del frontend:
        // el método solo recibe IDs y todo el cálculo se hace internamente.
        verify(usuarioRepository).findById(USUARIO_ID);
        verify(carritoService).obtenerOCrearCarritoParaUsuario(USUARIO_ID);
        verify(estadoPedidoRepository).findByNombre("CREADO");
    }

    // ===========================================
    // 2) Carrito vacío lanza IllegalStateException
    // ===========================================
    @Test
    @DisplayName("No se puede confirmar pedido si el carrito está vacío (IllegalStateException)")
    void confirmarPedidoDesdeCarrito_carritoVacioLanzaIllegalStateException() {
        // GIVEN
        Usuario usuario = new Usuario();
        usuario.setId(USUARIO_ID);

        Carrito carritoVacio = new Carrito();
        carritoVacio.setUsuario(usuario);
        carritoVacio.setItems(List.of());

        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(carritoService.obtenerOCrearCarritoParaUsuario(USUARIO_ID)).thenReturn(carritoVacio);

        // WHEN / THEN
        assertThrows(
                IllegalStateException.class,
                () -> pedidoService.confirmarPedidoDesdeCarrito(USUARIO_ID, DIRECCION_ID, METODO_PAGO_ID)
        );

        // No se debe intentar tocar dirección, método de pago ni guardar pedido
        verifyNoInteractions(direccionRepository, metodoPagoRepository, estadoPedidoRepository, pedidoRepository);
    }

    // =================================================
    // 3) Delegación del vaciado del carrito a CarritoService
    // =================================================
    @Test
    @DisplayName("confirmarPedidoDesdeCarrito delega vaciado del carrito en CarritoService.vaciarCarrito")
    void confirmarPedidoDesdeCarrito_delegaVaciadoCarritoEnCarritoService() {
        // GIVEN
        Usuario usuario = new Usuario();
        usuario.setId(USUARIO_ID);

        Direccion direccion = new Direccion();
        direccion.setId(DIRECCION_ID);
        direccion.setUsuario(usuario);

        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setId(METODO_PAGO_ID);
        metodoPago.setUsuario(usuario);

        EstadoPedido estadoCreado = new EstadoPedido();
        estadoCreado.setNombre("CREADO");

        Producto producto = new Producto();
        producto.setId(100L);
        producto.setNombre("Producto X");
        producto.setPrecio(10.0);
        producto.setPorcentajeDescuento(0);
        producto.setStock(5);

        ItemCarrito item = new ItemCarrito();
        item.setProducto(producto);
        item.setCantidad(1);

        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setItems(List.of(item));

        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(carritoService.obtenerOCrearCarritoParaUsuario(USUARIO_ID)).thenReturn(carrito);
        when(direccionRepository.findById(DIRECCION_ID)).thenReturn(Optional.of(direccion));
        when(metodoPagoRepository.findById(METODO_PAGO_ID)).thenReturn(Optional.of(metodoPago));
        when(estadoPedidoRepository.findByNombre("CREADO")).thenReturn(Optional.of(estadoCreado));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        pedidoService.confirmarPedidoDesdeCarrito(USUARIO_ID, DIRECCION_ID, METODO_PAGO_ID);

        // THEN
        verify(carritoService).vaciarCarrito(USUARIO_ID);
        verifyNoMoreInteractions(carritoService);
    }

    // ========================================================
    // 4) Estado inicial del pedido = "CREADO" (decisión backend)
    // ========================================================
    @Test
    @DisplayName("confirmarPedidoDesdeCarrito asigna estado inicial CREADO usando EstadoPedidoRepository")
    void confirmarPedidoDesdeCarrito_asignaEstadoInicialCreadoDesdeBackend() {
        // GIVEN
        Usuario usuario = new Usuario();
        usuario.setId(USUARIO_ID);

        Direccion direccion = new Direccion();
        direccion.setId(DIRECCION_ID);
        direccion.setUsuario(usuario);

        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setId(METODO_PAGO_ID);
        metodoPago.setUsuario(usuario);

        EstadoPedido estadoCreado = new EstadoPedido();
        estadoCreado.setNombre("CREADO");

        Producto producto = new Producto();
        producto.setId(100L);
        producto.setNombre("Producto X");
        producto.setPrecio(10.0);
        producto.setPorcentajeDescuento(0);
        producto.setStock(5);

        ItemCarrito item = new ItemCarrito();
        item.setProducto(producto);
        item.setCantidad(1);

        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setItems(List.of(item));

        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(carritoService.obtenerOCrearCarritoParaUsuario(USUARIO_ID)).thenReturn(carrito);
        when(direccionRepository.findById(DIRECCION_ID)).thenReturn(Optional.of(direccion));
        when(metodoPagoRepository.findById(METODO_PAGO_ID)).thenReturn(Optional.of(metodoPago));
        when(estadoPedidoRepository.findByNombre("CREADO")).thenReturn(Optional.of(estadoCreado));

        ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
        when(pedidoRepository.save(pedidoCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        Pedido pedidoResultado = pedidoService.confirmarPedidoDesdeCarrito(
                USUARIO_ID, DIRECCION_ID, METODO_PAGO_ID);

        // THEN
        verify(estadoPedidoRepository).findByNombre("CREADO");

        Pedido pedidoGuardado = pedidoCaptor.getValue();
        assertEquals("CREADO", pedidoGuardado.getEstado().getNombre());
        assertEquals("CREADO", pedidoResultado.getEstado().getNombre());
    }
}

