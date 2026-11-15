package com.sanaesencia.profesional.model.dto;
import java.util.List;
import jakarta.validation.constraints.*;
public record ProfesionalRequest(
    @NotBlank @Size(max=120) String nombre,
    @NotBlank @Size(max=120) String tituloProfesional,
    @NotEmpty List<@NotNull @Positive Long> especialidadesIds,
    @NotEmpty List<@NotBlank String> modalidades,
    @NotBlank @Size(max=30) String estadoAgenda
) {}
