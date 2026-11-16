package com.sanaesencia.profesional.model.entity;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "profesionales")
public class Profesional {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String nombre;
    @Column(nullable = false, length = 120)
    private String tituloProfesional;

    @ElementCollection
    @CollectionTable(name = "profesional_especialidades", joinColumns = @JoinColumn(name = "profesional_id"))
    @Column(name = "especialidad_id", nullable = false)
    private List<Long> especialidadesIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "profesional_modalidades", joinColumns = @JoinColumn(name = "profesional_id"))
    @Column(name = "modalidad", nullable = false, length = 30)
    private List<String> modalidades = new ArrayList<>();

    @Column(nullable = false, length = 30)
    private String estadoAgenda = "DISPONIBLE";

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTituloProfesional() { return tituloProfesional; }
    public void setTituloProfesional(String tituloProfesional) { this.tituloProfesional = tituloProfesional; }
    public List<Long> getEspecialidadesIds() { return especialidadesIds; }
    public void setEspecialidadesIds(List<Long> especialidadesIds) { this.especialidadesIds = especialidadesIds; }
    public List<String> getModalidades() { return modalidades; }
    public void setModalidades(List<String> modalidades) { this.modalidades = modalidades; }
    public String getEstadoAgenda() { return estadoAgenda; }
    public void setEstadoAgenda(String estadoAgenda) { this.estadoAgenda = estadoAgenda; }
}
