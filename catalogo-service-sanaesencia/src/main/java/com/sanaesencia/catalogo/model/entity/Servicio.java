package com.sanaesencia.catalogo.model.entity;
import jakarta.persistence.*;
@Entity
@Table(name="servicios")
public class Servicio {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=120) private String nombre;
    @Column(nullable=false) private Long especialidadId;
    @Column(nullable=false) private Integer duracionMinutos;
    public Servicio() {}
    public Long getId() { return id; } public void setId(Long id) { this.id=id; }
    public String getNombre() { return nombre; } public void setNombre(String nombre) { this.nombre=nombre; }
    public Long getEspecialidadId() { return especialidadId; } public void setEspecialidadId(Long id) { this.especialidadId=id; }
    public Integer getDuracionMinutos() { return duracionMinutos; } public void setDuracionMinutos(Integer d) { this.duracionMinutos=d; }
}
