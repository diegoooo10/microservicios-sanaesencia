package com.sanaesencia.catalogo.service;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sanaesencia.catalogo.model.entity.*;
import com.sanaesencia.catalogo.model.dto.*;
import com.sanaesencia.catalogo.repository.*;
@Service
@Transactional
public class CatalogoService {
    private final EspecialidadRepository especialidades;
    private final ServicioRepository servicios;
    public CatalogoService(EspecialidadRepository e, ServicioRepository s) { this.especialidades=e; this.servicios=s; }

    public List<Especialidad> listarEspecialidades() { return especialidades.findAll(); }
    public Especialidad obtenerEspecialidad(Long id) { return especialidades.findById(id).orElseThrow(() -> new IllegalArgumentException("Especialidad no encontrada: "+id)); }
    public Especialidad crearEspecialidad(EspecialidadRequest r) {
        Especialidad e=new Especialidad(); e.setNombre(r.nombre()); e.setDescripcion(r.descripcion()); return especialidades.save(e);
    }
    public Especialidad actualizarEspecialidad(Long id, EspecialidadRequest r) {
        Especialidad e=obtenerEspecialidad(id); e.setNombre(r.nombre()); e.setDescripcion(r.descripcion()); return especialidades.save(e);
    }
    public void eliminarEspecialidad(Long id) {
        if (!servicios.findByEspecialidadId(id).isEmpty()) throw new IllegalArgumentException("No se puede eliminar: existen servicios asociados");
        especialidades.delete(obtenerEspecialidad(id));
    }

    public List<Servicio> listarServicios(Long especialidadId) {
        if (especialidadId == null) return servicios.findAll();
        return servicios.findByEspecialidadId(especialidadId);
    }
    public Servicio obtenerServicio(Long id) { return servicios.findById(id).orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado: "+id)); }
    public Servicio crearServicio(ServicioRequest r) {
        obtenerEspecialidad(r.especialidadId());
        Servicio s=new Servicio(); s.setNombre(r.nombre()); s.setEspecialidadId(r.especialidadId()); s.setDuracionMinutos(r.duracionMinutos()); return servicios.save(s);
    }
    public Servicio actualizarServicio(Long id, ServicioRequest r) {
        obtenerEspecialidad(r.especialidadId()); Servicio s=obtenerServicio(id);
        s.setNombre(r.nombre()); s.setEspecialidadId(r.especialidadId()); s.setDuracionMinutos(r.duracionMinutos()); return servicios.save(s);
    }
    public void eliminarServicio(Long id) { servicios.delete(obtenerServicio(id)); }
}
