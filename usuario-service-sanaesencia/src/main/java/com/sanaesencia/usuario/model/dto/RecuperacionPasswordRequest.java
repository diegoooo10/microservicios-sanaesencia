
package com.sanaesencia.usuario.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RecuperacionPasswordRequest(

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es válido")
        String correo

) {
}
