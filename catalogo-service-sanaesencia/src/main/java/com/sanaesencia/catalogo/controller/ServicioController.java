package com.sanaesencia.catalogo.controller;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sanaesencia.catalogo.model.entity.Servicio;
import com.sanaesencia.catalogo.model.dto.ServicioRequest;
import com.sanaesencia.catalogo.service.CatalogoService;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/servicios")
public class ServicioController {
    private final CatalogoService service;
    public ServicioController(CatalogoService service) { this.service=service; }
    @GetMapping public List<Servicio> listar(@RequestParam(required=false) Long especialidadId) { return service.listarServicios(especialidadId); }
    @GetMapping("/<built-in function id>") public Servicio obtener(@PathVariable Long id) { return service.obtenerServicio(id); }
    @PostMapping public ResponseEntity<Servicio> crear(@Valid @RequestBody ServicioRequest r) {
        Servicio creado=service.crearServicio(r); return ResponseEntity.created(URI.create("/api/servicios/"+creado.getId())).body(creado);
    }
    @PutMapping("/<built-in function id>") public Servicio actualizar(@PathVariable Long id,@Valid @RequestBody ServicioRequest r) { return service.actualizarServicio(id,r); }
    @DeleteMapping("/<built-in function id>") public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.eliminarServicio(id); return ResponseEntity.noContent().build(); }
}
