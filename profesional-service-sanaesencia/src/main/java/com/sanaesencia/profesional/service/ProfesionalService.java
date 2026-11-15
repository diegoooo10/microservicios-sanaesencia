package com.sanaesencia.profesional.service;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sanaesencia.profesional.model.dto.ProfesionalRequest;
import com.sanaesencia.profesional.model.entity.Profesional;
import com.sanaesencia.profesional.repository.ProfesionalRepository;
@Service
@Transactional
public class ProfesionalService {
    private final ProfesionalRepository repository;
    public ProfesionalService(ProfesionalRepository repository) {this.repository=repository;}
    public List<Profesional> listar(Long especialidadId) {
        return especialidadId == null ? repository.findAll() : repository.findDistinctByEspecialidadesIdsContaining(especialidadId);
    }
    public Profesional obtener(Long id) {return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Profesional no encontrado: "+id));}
    public Profesional crear(ProfesionalRequest r) {
        Profesional p=new Profesional(); aplicar(r,p); return repository.save(p);
    }
    public Profesional actualizar(Long id, ProfesionalRequest r) {
        Profesional p=obtener(id); aplicar(r,p); return repository.save(p);
    }
    public void eliminar(Long id) {repository.delete(obtener(id));}
    private void aplicar(ProfesionalRequest r, Profesional p) {
        p.setNombre(r.nombre()); p.setTituloProfesional(r.tituloProfesional());
        p.setEspecialidadesIds(r.especialidadesIds()); p.setModalidades(r.modalidades());
        p.setEstadoAgenda(r.estadoAgenda());
    }
}
