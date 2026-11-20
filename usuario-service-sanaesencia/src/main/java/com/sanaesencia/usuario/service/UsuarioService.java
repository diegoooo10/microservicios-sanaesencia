
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
import com.sanaesencia.usuario.exception.CredencialesInvalidasException;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public UsuarioService(
            UsuarioRepository repository,
            UsuarioMapper mapper,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            EmailService emailService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
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
            new CredencialesInvalidasException("Credenciales inválidas")
    );

    if (!passwordEncoder.matches(
            request.password(),
            usuario.getPassword()
    )) {
        throw new CredencialesInvalidasException(
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


        public void solicitarRecuperacion(String correo) {

                String correoNormalizado = correo.trim().toLowerCase();

                repository.findByCorreo(correoNormalizado)
                        .ifPresent(usuario -> {

                                String token = jwtService.generatePasswordResetToken(usuario);

                                String enlace = "https://sanaesencia-app.duckdns.org/"
                                        + "reset-password?token=" + token;

                                emailService.enviarRecuperacion(
                                        usuario.getCorreo(),
                                        enlace
                                );
                        });
                }

        public void restablecerPassword(String token, String nuevaPassword) {

        // Validar el JWT y obtener el ID del usuario
        Long usuarioId = Long.parseLong(
                jwtService.validateAndGetClaims(token).getSubject()
        );

        // Buscar al usuario en la base de datos
        Usuario usuario = repository.findWithLockById(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Token de recuperación inválido"
                        )
                );

        // Validar finalidad, identidad y huella de la contraseña
        jwtService.validatePasswordResetToken(token, usuario);

        // Cifrar y guardar la nueva contraseña
        usuario.setPassword(
                passwordEncoder.encode(nuevaPassword)
        );

        repository.save(usuario);
        }

}
