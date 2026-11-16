
package com.sanaesencia.profesional.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.sanaesencia.profesional.model.dto.ProfesionalResponse;
import com.sanaesencia.profesional.service.ProfesionalService;

@RestController
@RequestMapping("/api/profesionales")
@SecurityRequirement(name = "Bearer Authentication")
public class ProfesionalController {

    private final ProfesionalService service;

    public ProfesionalController(ProfesionalService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar profesionales, opcionalmente filtrados por especialidad")
    public List<ProfesionalResponse> listar(
            @RequestParam(required = false) Long especialidadId
    ) {
        return service.listar(especialidadId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un profesional por su identificador")
    public ProfesionalResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }
}
