
package com.sanaesencia.reserva.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.sanaesencia.reserva.model.enums.EstadoReserva;

import jakarta.persistence.*;

@Entity
@Table(name = "reservas", indexes = {
        @Index(
                name = "idx_reserva_profesional_fecha",
                columnList = "profesional_id,fecha"
        ),
        @Index(
                name = "idx_reserva_usuario",
                columnList = "usuario_id"
        )
})
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "usuario_id")
    private Long usuarioId;

    @Column(nullable = false, name = "profesional_id")
    private Long profesionalId;

    @Column(nullable = false, name = "servicio_id")
    private Long servicioId;

    @Column(nullable = false, length = 100)
    private String especialidad;

    @Column(nullable = false, length = 120)
    private String servicio;

    @Column(nullable = false, length = 120)
    private String profesional;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(nullable = false, length = 30)
    private String modalidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado;

    @Column(nullable = false)
    private Integer duracionMinutos;

    @Column(name = "fecha_expiracion_pago")
    private LocalDateTime fechaExpiracionPago;

    public Long getId() { return id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long v) { usuarioId = v; }

    public Long getProfesionalId() { return profesionalId; }
    public void setProfesionalId(Long v) { profesionalId = v; }

    public Long getServicioId() { return servicioId; }
    public void setServicioId(Long v) { servicioId = v; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String v) { especialidad = v; }

    public String getServicio() { return servicio; }
    public void setServicio(String v) { servicio = v; }

    public String getProfesional() { return profesional; }
    public void setProfesional(String v) { profesional = v; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate v) { fecha = v; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime v) { hora = v; }

    public String getModalidad() { return modalidad; }
    public void setModalidad(String v) { modalidad = v; }

    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva v) { estado = v; }

    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer v) { duracionMinutos = v; }

    public LocalDateTime getFechaExpiracionPago() { return fechaExpiracionPago;}

     public void setFechaExpiracionPago(LocalDateTime fechaExpiracionPago) {
         this.fechaExpiracionPago = fechaExpiracionPago; }
}
