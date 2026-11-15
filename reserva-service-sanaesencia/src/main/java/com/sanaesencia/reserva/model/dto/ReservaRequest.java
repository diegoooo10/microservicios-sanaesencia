package com.sanaesencia.reserva.model.dto;
import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.*;
public record ReservaRequest(
    @NotNull @Positive Long profesionalId,
    @NotNull @Positive Long servicioId,
    @NotBlank @Size(max=100) String especialidad,
    @NotBlank @Size(max=120) String servicio,
    @NotBlank @Size(max=120) String profesional,
    @NotNull @FutureOrPresent LocalDate fecha,
    @NotNull LocalTime hora,
    @NotBlank @Pattern(regexp="Presencial|Online", message="La modalidad debe ser Presencial u Online") String modalidad,
    @NotNull @Positive Integer duracionMinutos
) {}
