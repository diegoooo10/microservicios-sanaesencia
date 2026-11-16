package com.sanaesencia.servicio.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sanaesencia.servicio.model.entity.Servicio;
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    List<Servicio> findByEspecialidadId(Long especialidadId);
}
