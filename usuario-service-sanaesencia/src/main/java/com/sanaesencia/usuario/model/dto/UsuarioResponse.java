package com.sanaesencia.usuario.model.dto;

import java.time.LocalDate;

import com.sanaesencia.usuario.model.enums.Rol;

public record UsuarioResponse(
        Long id,
        String nombre,
        String apellido,
        String telefono,
        LocalDate fechaNacimiento,
        String ciudad,
        String correo,
        Rol rol
) {
}
