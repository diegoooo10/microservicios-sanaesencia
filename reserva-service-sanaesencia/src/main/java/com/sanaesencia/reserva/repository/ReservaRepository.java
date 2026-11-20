
package com.sanaesencia.reserva.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sanaesencia.reserva.model.entity.Reserva;
import com.sanaesencia.reserva.model.enums.EstadoReserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUsuarioIdOrderByFechaAscHoraAsc(Long usuarioId);

    List<Reserva> findByProfesionalIdAndFechaAndEstadoIn(
            Long profesionalId,
            LocalDate fecha,
            List<EstadoReserva> estados
    );

    Optional<Reserva> findByIdAndUsuarioId(
            Long id,
            Long usuarioId
    );

    List<Reserva> findByEstadoAndFechaExpiracionPagoLessThanEqual(
            EstadoReserva estado,
            LocalDateTime fechaExpiracionPago
    );
}
