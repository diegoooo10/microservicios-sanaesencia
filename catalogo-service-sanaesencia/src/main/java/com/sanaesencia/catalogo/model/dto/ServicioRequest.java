package com.sanaesencia.catalogo.model.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
public record ServicioRequest(@NotBlank @Size(max=120) String nombre, @NotNull @Positive Long especialidadId, @NotNull @Positive Integer duracionMinutos) {}
