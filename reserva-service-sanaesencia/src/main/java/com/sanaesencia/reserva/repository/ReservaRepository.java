package com.sanaesencia.reserva.repository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sanaesencia.reserva.model.entity.Reserva;
import com.sanaesencia.reserva.model.enums.EstadoReserva;
public interface ReservaRepository extends JpaRepository<Reserva,Long> {
    List<Reserva> findByUsuarioCorreoOrderByFechaAscHoraAsc(String correo);
    List<Reserva> findByUsuarioCorreoAndEstadoOrderByFechaAscHoraAsc(String correo, EstadoReserva estado);
    List<Reserva> findByProfesionalIdAndFechaAndEstadoIn(Long profesionalId, LocalDate fecha, List<EstadoReserva> estados);
}
