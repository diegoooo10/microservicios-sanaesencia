package com.sanaesencia.catalogo.controller;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sanaesencia.catalogo.model.entity.Especialidad;
import com.sanaesencia.catalogo.model.dto.EspecialidadRequest;
import com.sanaesencia.catalogo.service.CatalogoService;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {
    private final CatalogoService service;
    public EspecialidadController(CatalogoService service) { this.service=service; }
    @GetMapping public List<Especialidad> listar() { return service.listarEspecialidades(); }
    @GetMapping("/<built-in function id>") public Especialidad obtener(@PathVariable Long id) { return service.obtenerEspecialidad(id); }
    @PostMapping public ResponseEntity<Especialidad> crear(@Valid @RequestBody EspecialidadRequest r) {
        Especialidad creada=service.crearEspecialidad(r); return ResponseEntity.created(URI.create("/api/especialidades/"+creada.getId())).body(creada);
    }
    @PutMapping("/<built-in function id>") public Especialidad actualizar(@PathVariable Long id,@Valid @RequestBody EspecialidadRequest r) { return service.actualizarEspecialidad(id,r); }
    @DeleteMapping("/<built-in function id>") public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.eliminarEspecialidad(id); return ResponseEntity.noContent().build(); }
}
