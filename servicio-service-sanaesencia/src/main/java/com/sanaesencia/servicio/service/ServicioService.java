package com.sanaesencia.servicio.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sanaesencia.servicio.mapper.CatalogoMapper;
import com.sanaesencia.servicio.model.dto.*;
import com.sanaesencia.servicio.repository.*;

@Service
@Transactional(readOnly = true)
public class ServicioService {
    private final EspecialidadRepository especialidadRepository;
    private final ServicioRepository servicioRepository;
    private final CatalogoMapper mapper;

    public ServicioService(EspecialidadRepository er, ServicioRepository sr, CatalogoMapper mapper) {
        this.especialidadRepository = er;
        this.servicioRepository = sr;
        this.mapper = mapper;
    }

    public List<EspecialidadResponse> listarEspecialidades() {
        return especialidadRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    public EspecialidadResponse obtenerEspecialidad(Long id) {
        return especialidadRepository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Especialidad no encontrada: " + id));
    }

    public List<ServicioResponse> listarServicios(Long especialidadId) {
        var servicios = especialidadId == null
                ? servicioRepository.findAll()
                : servicioRepository.findByEspecialidadId(especialidadId);
        return servicios.stream().map(mapper::toResponse).toList();
    }

    public ServicioResponse obtenerServicio(Long id) {
        return servicioRepository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado: " + id));
    }
}
