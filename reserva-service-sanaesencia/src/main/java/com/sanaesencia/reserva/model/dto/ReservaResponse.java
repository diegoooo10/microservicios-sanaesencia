package com.sanaesencia.reserva.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaResponse(
        Long id,
        Long profesionalId,
        Long servicioId,
        String especialidad,
        String servicio,
        String profesional,
        LocalDate fecha,
        LocalTime hora,
        String modalidad,
        String estado,
        Integer duracionMinutos
) {
}
