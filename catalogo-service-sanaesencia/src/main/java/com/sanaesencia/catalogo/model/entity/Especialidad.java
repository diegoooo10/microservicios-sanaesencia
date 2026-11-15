package com.sanaesencia.catalogo.model.entity;
import jakarta.persistence.*;
@Entity
@Table(name="especialidades")
public class Especialidad {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=100) private String nombre;
    @Column(nullable=false, length=500) private String descripcion;
    public Especialidad() {}
    public Long getId() { return id; } public void setId(Long id) { this.id=id; }
    public String getNombre() { return nombre; } public void setNombre(String nombre) { this.nombre=nombre; }
    public String getDescripcion() { return descripcion; } public void setDescripcion(String descripcion) { this.descripcion=descripcion; }
}
