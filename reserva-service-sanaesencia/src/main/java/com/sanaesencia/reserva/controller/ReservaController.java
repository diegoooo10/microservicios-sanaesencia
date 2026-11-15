package com.sanaesencia.reserva.controller;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.sanaesencia.reserva.model.dto.ReservaRequest;
import com.sanaesencia.reserva.model.entity.Reserva;
import com.sanaesencia.reserva.service.ReservaService;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    private final ReservaService service;
    public ReservaController(ReservaService service) {this.service=service;}

    @PostMapping
    public ResponseEntity<Reserva> crear(Authentication auth, @Valid @RequestBody ReservaRequest request) {
        Reserva creada=service.crear(auth.getName(),request);
        return ResponseEntity.created(URI.create("/api/reservas/"+creada.getId())).body(creada);
    }

    @GetMapping("/mis-reservas")
    public List<Reserva> listarPropias(Authentication auth) {return service.listarPropias(auth.getName());}

    @GetMapping("/mis-reservas/<built-in function id>")
    public Reserva obtenerPropia(Authentication auth,@PathVariable Long id) {return service.obtenerPropia(id,auth.getName());}

    @PatchMapping("/mis-reservas/<built-in function id>/cancelacion")
    public Reserva cancelar(Authentication auth,@PathVariable Long id) {return service.cancelar(id,auth.getName());}

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public List<Reserva> listarTodas() {return service.listarTodas();}
}
