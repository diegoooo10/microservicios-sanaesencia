
package com.sanaesencia.usuario.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanaesencia.usuario.mapper.UsuarioMapper;
import com.sanaesencia.usuario.model.dto.*;
import com.sanaesencia.usuario.model.entity.Usuario;
import com.sanaesencia.usuario.model.enums.Rol;
import com.sanaesencia.usuario.repository.UsuarioRepository;
import com.sanaesencia.usuario.security.JwtService;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(
            UsuarioRepository repository,
            UsuarioMapper mapper,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UsuarioResponse registrar(UsuarioRequest request) {

        String correo = request.correo().trim().toLowerCase();

        if (repository.existsByCorreo(correo)) {
            throw new IllegalArgumentException(
                    "El correo ya está registrado"
            );
        }

        Usuario usuario = mapper.toEntity(request);

        usuario.setPassword(
                passwordEncoder.encode(request.password())
        );

        usuario.setRol(Rol.CLIENTE);

        return mapper.toResponse(repository.save(usuario));
    }

    public LoginResponse iniciarSesion(LoginRequest request) {

        Usuario usuario = repository.findByCorreo(
                request.correo().trim().toLowerCase()
        ).orElseThrow(() ->
                new IllegalArgumentException("Credenciales inválidas")
        );

        if (!passwordEncoder.matches(
                request.password(),
                usuario.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "Credenciales inválidas"
            );
        }

        return new LoginResponse(
                jwtService.generateToken(usuario),
                "Bearer",
                usuario.getCorreo(),
                usuario.getRol().name(),
                jwtService.getExpirationMs()
        );
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPerfil(Long userId) {

        return repository.findById(userId)
                .map(mapper::toResponse)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuario no encontrado")
                );
    }

    public UsuarioResponse actualizarPerfil(
            Long userId,
            UsuarioUpdateRequest request
    ) {

        Usuario usuario = repository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuario no encontrado")
                );

        if (request.correo() == null
                && request.direccion() == null
                && request.telefono() == null) {

            throw new IllegalArgumentException(
                    "Debe proporcionar al menos un dato para actualizar"
            );
        }

        if (request.correo() != null) {

            String nuevoCorreo = request.correo()
                    .trim()
                    .toLowerCase();

            if (nuevoCorreo.isBlank()) {
                throw new IllegalArgumentException(
                        "El correo no puede estar vacío"
                );
            }

            if (!nuevoCorreo.equals(usuario.getCorreo())
                    && repository.existsByCorreoAndIdNot(
                            nuevoCorreo,
                            usuario.getId()
                    )) {

                throw new IllegalArgumentException(
                        "El correo ya está registrado"
                );
            }

            usuario.setCorreo(nuevoCorreo);
        }

        if (request.direccion() != null) {
            usuario.setDireccion(request.direccion().trim());
        }

        if (request.telefono() != null) {
            usuario.setTelefono(request.telefono().trim());
        }

        return mapper.toResponse(repository.save(usuario));
    }
}
