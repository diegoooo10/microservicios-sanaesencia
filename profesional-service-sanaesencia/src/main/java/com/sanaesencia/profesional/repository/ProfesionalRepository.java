package com.sanaesencia.profesional.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sanaesencia.profesional.model.entity.Profesional;
public interface ProfesionalRepository extends JpaRepository<Profesional,Long> {
    List<Profesional> findDistinctByEspecialidadesIdsContaining(Long especialidadId);
}
