
package com.sanaesencia.usuario.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.sanaesencia.usuario.model.dto.*;
import com.sanaesencia.usuario.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de cuentas de clientes")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar una nueva cuenta de cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente registrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Correo electrónico")
    })
    public UsuarioResponse registrar(
            @Valid @RequestBody UsuarioRequest request
    ) {
        return service.registrar(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener un JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public LoginResponse iniciarSesion(
            @Valid @RequestBody LoginRequest request
    ) {
        return service.iniciarSesion(request);
    }

    @GetMapping("/perfil")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Consultar el perfil del cliente autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public UsuarioResponse perfil(Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        return service.obtenerPerfil(userId);
    }

    @PatchMapping("/perfil")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Actualizar los datos de contacto del cliente autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "409", description = "Correo ya registrado")
    })
    public ResponseEntity<UsuarioResponse> actualizar(
            Authentication authentication,
            @Valid @RequestBody UsuarioUpdateRequest request
    ) {

        Long userId = (Long) authentication.getPrincipal();

        UsuarioResponse actualizado = service.actualizarPerfil(
                userId,
                request
        );

        return ResponseEntity.ok(actualizado);
    }
}
