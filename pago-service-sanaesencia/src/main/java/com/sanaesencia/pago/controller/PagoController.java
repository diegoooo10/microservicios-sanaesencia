package com.sanaesencia.pago.controller;

import com.sanaesencia.pago.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.sanaesencia.pago.model.PagoDtos.*;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    // Endpoints backend a backend: requieren X-Internal-Key.
    @PostMapping
    public ResponseEntity<InicioResponse> crear(@Valid @RequestBody CrearPago request) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @GetMapping("/{id}")
    public PagoResponse obtener(@PathVariable Long id) {
        return service.dto(service.obtener(id));
    }

    @GetMapping
    public List<PagoResponse> listar(@RequestParam Long reservaId) {
        return service.listar(reservaId);
    }

    // Transbank retorna a esta URL (sin X-Internal-Key).
    @RequestMapping(value = "/webpay/retorno", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> retorno(@RequestParam Map<String, String> params) throws Exception {
        String token = params.get("token_ws");
        if (token == null || token.isBlank()) {
            return ResponseEntity.ok("Pago interrumpido. Consulta el estado de tu reserva en SanaEsencia.");
        }
        PagoResponse pago = service.confirmar(token);
        return ResponseEntity.ok("Estado del pago: " + pago.estado()
                + ". Vuelve a SanaEsencia para consultar tu reserva.");
    }

    @PostMapping("/{id}/devoluciones")
    public ResponseEntity<DevolucionResponse> devolver(@PathVariable Long id) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.devolver(id));
    }

    @GetMapping("/{id}/devoluciones")
    public List<DevolucionResponse> devoluciones(@PathVariable Long id) {
        return service.devoluciones(id);
    }
}
