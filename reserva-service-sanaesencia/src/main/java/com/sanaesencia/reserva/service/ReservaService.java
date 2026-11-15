package com.sanaesencia.reserva.service;
import java.time.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sanaesencia.reserva.model.dto.ReservaRequest;
import com.sanaesencia.reserva.model.entity.Reserva;
import com.sanaesencia.reserva.model.enums.EstadoReserva;
import com.sanaesencia.reserva.repository.ReservaRepository;
@Service
@Transactional
public class ReservaService {
    private static final long HORAS_MINIMAS_CANCELACION = 12;
    private final ReservaRepository repository;
    public ReservaService(ReservaRepository repository) {this.repository=repository;}

    public Reserva crear(String correo, ReservaRequest r) {
        LocalDateTime inicio = LocalDateTime.of(r.fecha(), r.hora());
        if (!inicio.isAfter(LocalDateTime.now())) throw new IllegalArgumentException("La fecha y hora deben ser futuras");
        LocalDateTime fin = inicio.plusMinutes(r.duracionMinutos());
        List<Reserva> existentes = repository.findByProfesionalIdAndFechaAndEstadoIn(
                r.profesionalId(), r.fecha(), List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA));
        boolean ocupado = existentes.stream().anyMatch(e -> {
            LocalDateTime otroInicio = LocalDateTime.of(e.getFecha(), e.getHora());
            LocalDateTime otroFin = otroInicio.plusMinutes(e.getDuracionMinutos());
            return inicio.isBefore(otroFin) && fin.isAfter(otroInicio);
        });
        if (ocupado) throw new IllegalArgumentException("El profesional ya tiene una reserva en ese horario");
        Reserva reserva=new Reserva();
        reserva.setUsuarioCorreo(correo);
        reserva.setProfesionalId(r.profesionalId()); reserva.setServicioId(r.servicioId());
        reserva.setEspecialidad(r.especialidad()); reserva.setServicio(r.servicio());
        reserva.setProfesional(r.profesional()); reserva.setFecha(r.fecha()); reserva.setHora(r.hora());
        reserva.setModalidad(r.modalidad()); reserva.setDuracionMinutos(r.duracionMinutos());
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return repository.save(reserva);
    }

    @Transactional(readOnly=true)
    public List<Reserva> listarPropias(String correo) {return repository.findByUsuarioCorreoOrderByFechaAscHoraAsc(correo);}

    @Transactional(readOnly=true)
    public Reserva obtenerPropia(Long id, String correo) {
        Reserva r=obtener(id);
        if (!r.getUsuarioCorreo().equals(correo)) throw new IllegalArgumentException("No tienes permiso para consultar esta reserva");
        return r;
    }

    public Reserva cancelar(Long id, String correo) {
        Reserva r=obtenerPropia(id,correo);
        if (r.getEstado()==EstadoReserva.CANCELADA) throw new IllegalArgumentException("La reserva ya está cancelada");
        if (r.getEstado()==EstadoReserva.REALIZADA || r.getEstado()==EstadoReserva.AUSENTE)
            throw new IllegalArgumentException("No se puede cancelar una reserva finalizada");
        LocalDateTime inicio=LocalDateTime.of(r.getFecha(),r.getHora());
        if (Duration.between(LocalDateTime.now(),inicio).toHours() < HORAS_MINIMAS_CANCELACION)
            throw new IllegalArgumentException("La cancelación requiere al menos 12 horas de anticipación");
        r.setEstado(EstadoReserva.CANCELADA);
        return repository.save(r);
    }

    @Transactional(readOnly=true)
    public List<Reserva> listarTodas() {return repository.findAll();}

    private Reserva obtener(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada: "+id));
    }
}
