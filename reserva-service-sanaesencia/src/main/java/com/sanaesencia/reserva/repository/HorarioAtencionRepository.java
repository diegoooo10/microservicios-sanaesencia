
package com.sanaesencia.reserva.repository;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sanaesencia.reserva.model.entity.HorarioAtencion;

public interface HorarioAtencionRepository
        extends JpaRepository<HorarioAtencion, Long> {

    List<HorarioAtencion> findByProfesionalIdAndDiaSemanaOrderByHoraInicioAsc(
            Long profesionalId,
            DayOfWeek diaSemana
    );

    List<HorarioAtencion> findByProfesionalIdOrderByDiaSemanaAscHoraInicioAsc(
            Long profesionalId
    );
}
