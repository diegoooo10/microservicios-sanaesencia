
package com.sanaesencia.reserva.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanaesencia.reserva.client.ProfesionalClient;
import com.sanaesencia.reserva.client.ServicioClient;
import com.sanaesencia.reserva.model.dto.ReservaRequest;
import com.sanaesencia.reserva.model.entity.Reserva;
import com.sanaesencia.reserva.model.enums.EstadoReserva;
import com.sanaesencia.reserva.repository.ReservaRepository;

@Service
@Transactional
public class ReservaService {

    private static final long HORAS_MINIMAS_CANCELACION = 12;

    private final ReservaRepository repository;
    private final ProfesionalClient profesionalClient;
    private final ServicioClient servicioClient;

    public ReservaService(
            ReservaRepository repository,
            ProfesionalClient profesionalClient,
            ServicioClient servicioClient
    ) {
        this.repository = repository;
        this.profesionalClient = profesionalClient;
        this.servicioClient = servicioClient;
    }

    public Reserva crear(
            Long userId,
            ReservaRequest request,
            String authorizationHeader
    ) {

        var profesional = profesionalClient.obtener(
                request.profesionalId(),
                authorizationHeader
        );

        var servicio = servicioClient.obtener(
                request.servicioId(),
                authorizationHeader
        );

        if (profesional == null || servicio == null) {
            throw new IllegalArgumentException(
                    "No se pudo validar el profesional o servicio"
            );
        }

        var especialidad = servicioClient.obtenerEspecialidad(
                servicio.especialidadId(),
                authorizationHeader
        );

        if (especialidad == null) {
            throw new IllegalArgumentException(
                    "No se pudo validar la especialidad"
            );
        }

        if (!profesional.especialidadesIds().contains(
                servicio.especialidadId()
        )) {
            throw new IllegalArgumentException(
                    "El profesional no ofrece la especialidad del servicio"
            );
        }

        if (!profesional.modalidades().contains(request.modalidad())) {
            throw new IllegalArgumentException(
                    "El profesional no atiende en la modalidad seleccionada"
            );
        }

        if (!"DISPONIBLE".equalsIgnoreCase(
                profesional.estadoAgenda()
        )) {
            throw new IllegalArgumentException(
                    "El profesional no tiene agenda disponible"
            );
        }

        LocalDateTime inicio = LocalDateTime.of(
                request.fecha(),
                request.hora()
        );

        if (!inicio.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "La fecha y hora deben ser futuras"
            );
        }

        LocalDateTime fin = inicio.plusMinutes(
                servicio.duracionMinutos()
        );

        List<Reserva> existentes =
                repository.findByProfesionalIdAndFechaAndEstadoIn(
                        profesional.id(),
                        request.fecha(),
                        List.of(EstadoReserva.CONFIRMADA)
                );

        boolean ocupado = existentes.stream().anyMatch(reserva -> {

            LocalDateTime otroInicio = LocalDateTime.of(
                    reserva.getFecha(),
                    reserva.getHora()
            );

            LocalDateTime otroFin = otroInicio.plusMinutes(
                    reserva.getDuracionMinutos()
            );

            return inicio.isBefore(otroFin)
                    && fin.isAfter(otroInicio);
        });

        if (ocupado) {
            throw new IllegalArgumentException(
                    "El profesional ya tiene una reserva en ese horario"
            );
        }

        Reserva reserva = new Reserva();

        reserva.setUsuarioId(userId);
        reserva.setProfesionalId(profesional.id());
        reserva.setServicioId(servicio.id());
        reserva.setEspecialidad(especialidad.nombre());
        reserva.setServicio(servicio.nombre());
        reserva.setProfesional(profesional.nombre());
        reserva.setFecha(request.fecha());
        reserva.setHora(request.hora());
        reserva.setModalidad(request.modalidad());
        reserva.setDuracionMinutos(servicio.duracionMinutos());
        reserva.setEstado(EstadoReserva.CONFIRMADA);

        return repository.save(reserva);
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarPropias(Long userId) {

        return repository.findByUsuarioIdOrderByFechaAscHoraAsc(userId);
    }

    @Transactional(readOnly = true)
    public Reserva obtenerPropia(Long id, Long userId) {

        return repository.findByIdAndUsuarioId(id, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Reserva no encontrada"
                        )
                );
    }

    public Reserva cancelar(Long id, Long userId) {

        Reserva reserva = obtenerPropia(id, userId);

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new IllegalArgumentException(
                    "La reserva no se puede cancelar"
            );
        }

        LocalDateTime inicio = LocalDateTime.of(
                reserva.getFecha(),
                reserva.getHora()
        );

        if (Duration.between(
                LocalDateTime.now(),
                inicio
        ).toHours() < HORAS_MINIMAS_CANCELACION) {

            throw new IllegalArgumentException(
                    "La cancelación requiere al menos 12 horas de anticipación"
            );
        }

        reserva.setEstado(EstadoReserva.CANCELADA);

        return repository.save(reserva);
    }
}
