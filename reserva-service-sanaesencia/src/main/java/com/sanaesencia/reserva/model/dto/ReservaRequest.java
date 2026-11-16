package com.sanaesencia.reserva.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.*;

public record ReservaRequest(
        @NotNull @Positive Long profesionalId,
        @NotNull @Positive Long servicioId,
        @NotNull @FutureOrPresent LocalDate fecha,
        @NotNull LocalTime hora,
        @NotBlank @Pattern(regexp = "Presencial|Online") String modalidad
) {
}
