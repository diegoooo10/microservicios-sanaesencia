package com.sanaesencia.usuario.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestablecerPasswordRequest(

        @NotBlank(message = "El token es obligatorio")
        String token,

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(
                min = 8,
                message = "La contraseña debe tener al menos 8 caracteres"
        )
        String nuevaPassword

){}