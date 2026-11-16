package com.sanaesencia.usuario.model.dto;

public record LoginResponse(
        String token, String tipo, String correo, String rol, long expiracionMs
) {
}
