package com.sanaesencia.profesional.model.dto;
import java.util.List;
public record ProfesionalResponse(
        Long id, String nombre, String tituloProfesional,
        List<Long> especialidadesIds, List<String> modalidades, String estadoAgenda
) {}
