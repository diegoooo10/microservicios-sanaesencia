package com.sanaesencia.usuario.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.sanaesencia.usuario.model.dto.*;
import com.sanaesencia.usuario.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrarUsuario(@Valid @RequestBody UsuarioRequest request) {
        return usuarioService.registrarUsuario(request);
    }

    @PostMapping("/login")
    public LoginResponse iniciarSesion(@Valid @RequestBody LoginRequest request) {
        return usuarioService.iniciarSesion(request);
    }

    @GetMapping("/perfil")
    public UsuarioResponse obtenerPerfil(Authentication authentication) {
        return usuarioService.obtenerPerfil(authentication.getName());
    }

    @PutMapping("/perfil")
    public UsuarioResponse actualizarPerfil(
            Authentication authentication,
            @Valid @RequestBody UsuarioUpdateRequest request) {
        return usuarioService.actualizarPerfil(authentication.getName(), request);
    }

    @GetMapping
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtenerUsuarioPorId(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
    }
}
