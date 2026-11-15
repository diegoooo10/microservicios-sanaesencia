package com.sanaesencia.profesional.controller;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sanaesencia.profesional.model.dto.ProfesionalRequest;
import com.sanaesencia.profesional.model.entity.Profesional;
import com.sanaesencia.profesional.service.ProfesionalService;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/profesionales")
public class ProfesionalController {
    private final ProfesionalService service;
    public ProfesionalController(ProfesionalService service) {this.service=service;}
    @GetMapping public List<Profesional> listar(@RequestParam(required=false) Long especialidadId) {return service.listar(especialidadId);}
    @GetMapping("/<built-in function id>") public Profesional obtener(@PathVariable Long id) {return service.obtener(id);}
    @PostMapping public ResponseEntity<Profesional> crear(@Valid @RequestBody ProfesionalRequest r) {
        Profesional p=service.crear(r); return ResponseEntity.created(URI.create("/api/profesionales/"+p.getId())).body(p);
    }
    @PutMapping("/<built-in function id>") public Profesional actualizar(@PathVariable Long id,@Valid @RequestBody ProfesionalRequest r) {return service.actualizar(id,r);}
    @DeleteMapping("/<built-in function id>") public ResponseEntity<Void> eliminar(@PathVariable Long id) {service.eliminar(id); return ResponseEntity.noContent().build();}
}
