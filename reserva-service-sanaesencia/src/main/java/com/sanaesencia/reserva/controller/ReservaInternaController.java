
package com.sanaesencia.reserva.controller;

import com.sanaesencia.reserva.mapper.ReservaMapper;
import com.sanaesencia.reserva.model.dto.ReservaResponse;
import com.sanaesencia.reserva.service.ReservaService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interno/reservas")
public class ReservaInternaController {

    private final ReservaService service;
    private final ReservaMapper mapper;

    public ReservaInternaController(
            ReservaService service,
            ReservaMapper mapper
    ) {
        this.service = service;
        this.mapper = mapper;
    }

    @PatchMapping("/{id}/confirmacion")
    public ResponseEntity<ReservaResponse> confirmarPago(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                mapper.toResponse(service.confirmarPago(id))
        );
    }
}
