
package com.sanaesencia.usuario.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequest(

        @Email(message = "El correo electrónico no tiene un formato válido")
        @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
        String correo,

        @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
        @Pattern(
                regexp = ".*\\S.*",
                message = "La dirección no puede estar vacía"
        )
        String direccion,

        @Pattern(
                regexp = "^\\+?[0-9 ()-]{8,20}$",
                message = "El teléfono no tiene un formato válido"
        )
        String telefono

) {
}
