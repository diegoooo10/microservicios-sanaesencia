
package com.sanaesencia.profesional.mapper;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.sanaesencia.profesional.model.entity.Profesional;
import com.sanaesencia.profesional.model.dto.ProfesionalResponse;

@Component
public class ProfesionalMapper {

    public ProfesionalResponse toResponse(Profesional p) {

        return new ProfesionalResponse(
                p.getId(),
                p.getNombre(),
                p.getTituloProfesional(),
                new ArrayList<>(p.getEspecialidadesIds()),
                new ArrayList<>(p.getModalidades()),
                p.getEstadoAgenda()
        );
    }
}
