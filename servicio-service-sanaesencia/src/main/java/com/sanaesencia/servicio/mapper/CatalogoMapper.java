package com.sanaesencia.servicio.mapper;
import org.springframework.stereotype.Component;
import com.sanaesencia.servicio.model.entity.*;
import com.sanaesencia.servicio.model.dto.*;
@Component
public class CatalogoMapper {
    public EspecialidadResponse toResponse(Especialidad e) {
        return new EspecialidadResponse(e.getId(), e.getNombre(), e.getDescripcion());
    }
    public ServicioResponse toResponse(Servicio s) {
        return new ServicioResponse(s.getId(), s.getNombre(), s.getEspecialidadId(), s.getDuracionMinutos(), s.getDescripcion());
    }
}
