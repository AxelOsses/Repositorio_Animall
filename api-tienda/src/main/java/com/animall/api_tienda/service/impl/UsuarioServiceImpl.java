package com.animall.api_tienda.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animall.api_tienda.dto.UsuarioCreateRequest;
import com.animall.api_tienda.dto.UsuarioUpdateRequest;
import com.animall.api_tienda.model.ConfiguracionUsuario;
import com.animall.api_tienda.model.Rol;
import com.animall.api_tienda.model.Usuario;
import com.animall.api_tienda.repository.RolRepository;
import com.animall.api_tienda.repository.UsuarioRepository;
import com.animall.api_tienda.service.UsuarioService;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public Usuario crear(UsuarioCreateRequest request) {
        Rol rol = rolRepository.findById(request.getRolId())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con id " + request.getRolId()));

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(request.getPassword());
        usuario.setRol(rol);
        usuario.setPuntos(0);
        usuario.setAhorroTotal(0.0);
        usuario.setFechaRegistro(LocalDate.now());
        usuario.setImagenPerfil(request.getImagenPerfil());

        if (request.getConfiguracion() != null) {
            ConfiguracionUsuario config = new ConfiguracionUsuario();
            config.setNotificaciones(request.getConfiguracion().getNotificaciones() != null
                    ? request.getConfiguracion().getNotificaciones() : true);
            config.setIdioma(request.getConfiguracion().getIdioma() != null
                    ? request.getConfiguracion().getIdioma() : "es");
            usuario.setConfiguracion(config);
            config.setUsuario(usuario);
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario actualizar(Long id, UsuarioUpdateRequest request) {
        return usuarioRepository.findById(id)
                .map(actual -> {
                    Rol rol = rolRepository.findById(request.getRolId())
                            .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con id " + request.getRolId()));

                    actual.setNombre(request.getNombre());
                    actual.setEmail(request.getEmail());
                    actual.setPassword(request.getPassword());
                    actual.setRol(rol);
                    actual.setImagenPerfil(request.getImagenPerfil());
                    // No se modifican puntos ni ahorroTotal (reglas de negocio)

                    if (request.getConfiguracion() != null) {
                        if (actual.getConfiguracion() == null) {
                            ConfiguracionUsuario config = new ConfiguracionUsuario();
                            config.setUsuario(actual);
                            actual.setConfiguracion(config);
                        }
                        actual.getConfiguracion().setNotificaciones(
                                request.getConfiguracion().getNotificaciones() != null
                                        ? request.getConfiguracion().getNotificaciones() : true);
                        actual.getConfiguracion().setIdioma(
                                request.getConfiguracion().getIdioma() != null
                                        ? request.getConfiguracion().getIdioma() : "es");
                    }

                    return usuarioRepository.save(actual);
                })
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id " + id));
    }

    @Override
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
}
