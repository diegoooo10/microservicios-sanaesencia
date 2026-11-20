
package com.sanaesencia.reserva.controller;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.sanaesencia.reserva.model.dto.ReservaRequest;
import com.sanaesencia.reserva.model.dto.ReservaResponse;
import com.sanaesencia.reserva.mapper.ReservaMapper;
import com.sanaesencia.reserva.model.entity.Reserva;
import com.sanaesencia.reserva.service.ReservaService;

import com.sanaesencia.reserva.client.PagoClient;

@RestController
@RequestMapping("/api/reservas")
@SecurityRequirement(name = "Bearer Authentication")
public class ReservaController {

    private final ReservaService service;
    private final ReservaMapper mapper;

    public ReservaController(
            ReservaService service,
            ReservaMapper mapper
    ) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Crear una reserva para el cliente autenticado")
    public ResponseEntity<ReservaResponse> crear(
            Authentication authentication,
            HttpServletRequest request,
            @Valid @RequestBody ReservaRequest reservaRequest
    ) {

        Long userId = (Long) authentication.getPrincipal();

        String authorizationHeader = request.getHeader(
                HttpHeaders.AUTHORIZATION
        );

        Reserva creada = service.crear(
                userId,
                reservaRequest,
                authorizationHeader
        );

        return ResponseEntity
                .created(URI.create("/api/reservas/" + creada.getId()))
                .body(mapper.toResponse(creada));
    }

    @GetMapping
    @Operation(summary = "Listar las reservas del cliente autenticado")
    public List<ReservaResponse> listar(
            Authentication authentication
    ) {

        Long userId = (Long) authentication.getPrincipal();

        return service.listarPropias(userId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar una reserva propia")
    public ReservaResponse obtener(
            Authentication authentication,
            @PathVariable Long id
    ) {

        Long userId = (Long) authentication.getPrincipal();

        return mapper.toResponse(
                service.obtenerPropia(id, userId)
        );
    }

    @PatchMapping("/{id}/cancelacion")
    @Operation(summary = "Cancelar una reserva propia con al menos 12 horas de anticipación")
    public ReservaResponse cancelar(
            Authentication authentication,
            @PathVariable Long id
    ) {

        Long userId = (Long) authentication.getPrincipal();

        return mapper.toResponse(
                service.cancelar(id, userId)
        );
    }

        @GetMapping("/disponibilidad")
        @Operation(summary = "Consultar horarios disponibles para una prestación")
        public List<LocalTime> obtenerHorariosDisponibles(
                HttpServletRequest request,
                @RequestParam Long profesionalId,
                @RequestParam Long servicioId,
                @RequestParam LocalDate fecha,
                @RequestParam String modalidad
        ) {

        String authorizationHeader = request.getHeader(
                HttpHeaders.AUTHORIZATION
        );

        return service.obtenerHorariosDisponibles(
                profesionalId,
                servicioId,
                fecha,
                modalidad,
                authorizationHeader
        );
        }


        @PostMapping("/{id}/pago")
        @Operation(summary = "Iniciar el pago de una reserva propia mediante Webpay")
        public PagoClient.InicioResponse iniciarPago(
                Authentication authentication,
                @PathVariable Long id
        ) {
        Long userId = (Long) authentication.getPrincipal();

        return service.iniciarPago(id, userId);
        }

}
