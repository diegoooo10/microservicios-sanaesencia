
package com.sanaesencia.reserva.model.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.persistence.*;

@Entity
@Table(
    name = "horarios_atencion",
    indexes = {
        @Index(
            name = "idx_horario_profesional_dia",
            columnList = "profesional_id,dia_semana"
        )
    }
)
public class HorarioAtencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "profesional_id")
    private Long profesionalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "dia_semana", length = 15)
    private DayOfWeek diaSemana;

    @Column(nullable = false, name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(nullable = false, name = "hora_fin")
    private LocalTime horaFin;

    public Long getId() {
        return id;
    }

    public Long getProfesionalId() {
        return profesionalId;
    }

    public void setProfesionalId(Long profesionalId) {
        this.profesionalId = profesionalId;
    }

    public DayOfWeek getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DayOfWeek diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }
}
