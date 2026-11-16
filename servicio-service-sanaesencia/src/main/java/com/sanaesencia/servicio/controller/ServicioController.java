
package com.sanaesencia.servicio.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.sanaesencia.servicio.model.dto.EspecialidadResponse;
import com.sanaesencia.servicio.model.dto.ServicioResponse;
import com.sanaesencia.servicio.service.ServicioService;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "Bearer Authentication")
public class ServicioController {

    private final ServicioService service;

    public ServicioController(ServicioService service) {
        this.service = service;
    }

    @GetMapping("/especialidades")
    @Operation(summary = "Listar especialidades disponibles")
    public List<EspecialidadResponse> listarEspecialidades() {
        return service.listarEspecialidades();
    }

    @GetMapping("/especialidades/{id}")
    @Operation(summary = "Consultar una especialidad por su identificador")
    public EspecialidadResponse obtenerEspecialidad(@PathVariable Long id) {
        return service.obtenerEspecialidad(id);
    }

    @GetMapping("/servicios")
    @Operation(summary = "Listar servicios, opcionalmente filtrados por especialidad")
    public List<ServicioResponse> listarServicios(
            @RequestParam(required = false) Long especialidadId
    ) {
        return service.listarServicios(especialidadId);
    }

    @GetMapping("/servicios/{id}")
    @Operation(summary = "Consultar un servicio por su identificador")
    public ServicioResponse obtenerServicio(@PathVariable Long id) {
        return service.obtenerServicio(id);
    }
}
