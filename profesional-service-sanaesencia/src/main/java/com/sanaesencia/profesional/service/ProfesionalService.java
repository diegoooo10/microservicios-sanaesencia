package com.sanaesencia.profesional.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sanaesencia.profesional.mapper.ProfesionalMapper;
import com.sanaesencia.profesional.model.dto.ProfesionalResponse;
import com.sanaesencia.profesional.repository.ProfesionalRepository;

@Service
@Transactional(readOnly = true)
public class ProfesionalService {
    private final ProfesionalRepository repository;
    private final ProfesionalMapper mapper;

    public ProfesionalService(ProfesionalRepository repository, ProfesionalMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ProfesionalResponse> listar(Long especialidadId) {
        var profesionales = especialidadId == null
                ? repository.findAll()
                : repository.findDistinctByEspecialidadesIdsContaining(especialidadId);
        return profesionales.stream().map(mapper::toResponse).toList();
    }

    public ProfesionalResponse obtener(Long id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Profesional no encontrado: " + id));
    }
}
