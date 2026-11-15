package com.sanaesencia.usuario.model.dto;

public record LoginResponse(
        String token,
        String tipo,
        UsuarioResponse usuario
) {
}
