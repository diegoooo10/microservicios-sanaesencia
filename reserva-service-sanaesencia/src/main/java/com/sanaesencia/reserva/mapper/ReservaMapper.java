package com.sanaesencia.reserva.mapper;

import org.springframework.stereotype.Component;
import com.sanaesencia.reserva.model.dto.ReservaResponse;
import com.sanaesencia.reserva.model.entity.Reserva;

@Component
public class ReservaMapper {
    public ReservaResponse toResponse(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getProfesionalId(),
                reserva.getServicioId(),
                reserva.getEspecialidad(),
                reserva.getServicio(),
                reserva.getProfesional(),
                reserva.getFecha(),
                reserva.getHora(),
                reserva.getModalidad(),
                reserva.getEstado().name(),
                reserva.getDuracionMinutos()
        );
    }
}
