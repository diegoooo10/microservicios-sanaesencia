
package com.sanaesencia.usuario.model.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.*;

public record UsuarioRequest(

        @NotBlank
        @Size(max = 80)
        String nombre,

        @NotBlank
        @Size(max = 80)
        String apellido,

        @NotBlank
        @Email
        @Size(max = 150)
        String correo,

        @NotBlank
        @Size(max = 20)
        String run,

        @NotNull
        @Past
        LocalDate fechaNacimiento,

        @NotBlank
        @Size(min = 8, max = 72)
        String password,

        @Size(max = 200)
        String direccion,

        @Pattern(regexp = "^$|^[0-9+()\\-\\s]{7,20}$",
                message = "El teléfono debe tener entre 7 y 20 caracteres válidos")
        String telefono

) {
}
