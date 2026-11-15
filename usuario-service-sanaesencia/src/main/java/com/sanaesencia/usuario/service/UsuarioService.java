package com.sanaesencia.usuario.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanaesencia.usuario.exception.CredencialesInvalidasException;
import com.sanaesencia.usuario.exception.UsuarioDuplicadoException;
import com.sanaesencia.usuario.exception.UsuarioNoEncontradoException;
import com.sanaesencia.usuario.mapper.UsuarioMapper;
import com.sanaesencia.usuario.model.dto.*;
import com.sanaesencia.usuario.model.entity.Usuario;
import com.sanaesencia.usuario.model.enums.Rol;
import com.sanaesencia.usuario.repository.UsuarioRepository;
import com.sanaesencia.usuario.security.JwtService;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            UsuarioMapper usuarioMapper,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UsuarioResponse registrarUsuario(UsuarioRequest request) {
        String correo = request.correo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new UsuarioDuplicadoException("Ya existe un usuario registrado con ese correo");
        }

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setRol(Rol.CLIENTE);

        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public LoginResponse iniciarSesion(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreo(request.correo().trim().toLowerCase())
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos");
        }

        String token = jwtService.generarToken(usuario);
        return new LoginResponse(token, "Bearer", usuarioMapper.toResponse(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPerfil(String correoAutenticado) {
        return usuarioMapper.toResponse(buscarPorCorreo(correoAutenticado));
    }

    public UsuarioResponse actualizarPerfil(String correoAutenticado, UsuarioUpdateRequest request) {
        Usuario usuario = buscarPorCorreo(correoAutenticado);
        String nuevoCorreo = request.correo().trim().toLowerCase();

        usuarioRepository.findByCorreo(nuevoCorreo).ifPresent(existente -> {
            if (!existente.getId().equals(usuario.getId())) {
                throw new UsuarioDuplicadoException("Ya existe otro usuario registrado con ese correo");
            }
        });

        usuarioMapper.actualizarPerfil(request, usuario);
        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerUsuarioPorId(Long id) {
        return usuarioMapper.toResponse(buscarPorId(id));
    }

    public void eliminarUsuario(Long id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }

    private Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsuarioNoEncontradoException("No se encontró el usuario autenticado"));
    }

    private Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("No se encontró el usuario con id " + id));
    }
}
