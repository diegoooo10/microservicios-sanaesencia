
package com.sanaesencia.reserva.service;

import java.time.Duration;
import java.time.LocalDate;
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
import com.sanaesencia.reserva.model.entity.HorarioAtencion;
import com.sanaesencia.reserva.repository.HorarioAtencionRepository;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;

@Service
@Transactional
public class ReservaService {

    private static final long HORAS_MINIMAS_CANCELACION = 12;

    private final ReservaRepository repository;
    private final ProfesionalClient profesionalClient;
    private final ServicioClient servicioClient;
    private final HorarioAtencionRepository horarioAtencionRepository;

    public ReservaService(
            ReservaRepository repository,
            ProfesionalClient profesionalClient,
            ServicioClient servicioClient,
            HorarioAtencionRepository horarioAtencionRepository
    ) {
        this.repository = repository;
        this.profesionalClient = profesionalClient;
        this.servicioClient = servicioClient;
        this.horarioAtencionRepository = horarioAtencionRepository;
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

                // Validar que la reserva esté dentro de la jornada
        DayOfWeek diaSemana = request.fecha().getDayOfWeek();

        List<HorarioAtencion> horariosAtencion =
                horarioAtencionRepository
                        .findByProfesionalIdAndDiaSemanaOrderByHoraInicioAsc(
                                profesional.id(),
                                diaSemana
                        );

        boolean dentroDeJornada = horariosAtencion.stream()
                .anyMatch(horario ->
                        !request.hora().isBefore(horario.getHoraInicio())
                        && !fin.toLocalTime().isAfter(horario.getHoraFin())
                );

        if (!dentroDeJornada) {
        throw new IllegalArgumentException(
                "El horario seleccionado está fuera de la jornada de atención"
        );
        }

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


    @Transactional(readOnly = true)
    public List<LocalTime> obtenerHorariosDisponibles(
            Long profesionalId,
            Long servicioId,
            LocalDate fecha,
            String modalidad,
            String authorizationHeader
    ) {

        // 1. Consultar profesional y prestación
        var profesional = profesionalClient.obtener(
                profesionalId,
                authorizationHeader
        );

        var servicio = servicioClient.obtener(
                servicioId,
                authorizationHeader
        );

        if (profesional == null || servicio == null) {
            throw new IllegalArgumentException(
                    "No se pudo validar el profesional o servicio"
            );
        }

        // 2. Validar relación entre profesional y especialidad
        if (!profesional.especialidadesIds().contains(
                servicio.especialidadId()
        )) {
            throw new IllegalArgumentException(
                    "El profesional no ofrece la especialidad del servicio"
            );
        }

        // 3. Validar modalidad
        if (!profesional.modalidades().contains(modalidad)) {
            throw new IllegalArgumentException(
                    "El profesional no atiende en la modalidad seleccionada"
            );
        }

        // 4. Validar estado de agenda
        if (!"DISPONIBLE".equalsIgnoreCase(
                profesional.estadoAgenda()
        )) {
            return List.of();
        }

        // 5. Obtener jornada del profesional para ese día
        DayOfWeek diaSemana = fecha.getDayOfWeek();

        List<HorarioAtencion> horarios =
                horarioAtencionRepository
                        .findByProfesionalIdAndDiaSemanaOrderByHoraInicioAsc(
                                profesionalId,
                                diaSemana
                        );

        if (horarios.isEmpty()) {
            return List.of();
        }

        // 6. Consultar reservas confirmadas del profesional
        List<Reserva> existentes =
                repository.findByProfesionalIdAndFechaAndEstadoIn(
                        profesionalId,
                        fecha,
                        List.of(EstadoReserva.CONFIRMADA)
                );

        // 7. Generar horarios disponibles
        List<LocalTime> disponibles = new ArrayList<>();

        LocalDateTime ahora = LocalDateTime.now();

        int duracion = servicio.duracionMinutos();

        for (HorarioAtencion horario : horarios) {

            LocalTime horaInicio = horario.getHoraInicio();
            LocalTime horaFin = horario.getHoraFin();

            for (
                    LocalTime hora = horaInicio;
                    !hora.plusMinutes(duracion).isAfter(horaFin);
                    hora = hora.plusMinutes(30)
            ) {

                LocalDateTime inicio = LocalDateTime.of(fecha, hora);

                LocalDateTime fin = inicio.plusMinutes(duracion);

                // Excluir horarios pasados
                if (!inicio.isAfter(ahora)) {
                    continue;
                }

                // Comprobar superposición con reservas existentes
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

                if (!ocupado) {
                    disponibles.add(hora);
                }
            }
        }

        // 8. Eliminar duplicados y ordenar resultados
        return disponibles.stream()
                .distinct()
                .sorted()
                .toList();
    }

}
