
package com.sanaesencia.usuario.model.dto;

import java.time.LocalDate;

public record UsuarioResponse(
        Long id,
        String nombre,
        String apellido,
        String correo,
        String run,
        LocalDate fechaNacimiento,
        String direccion,
        String telefono,
        String rol
) {
}
