
package com.sanaesencia.pago.controller;

import com.sanaesencia.pago.model.PagoDtos.CrearPago;
import com.sanaesencia.pago.model.PagoDtos.DevolucionResponse;
import com.sanaesencia.pago.model.PagoDtos.InicioResponse;
import com.sanaesencia.pago.model.PagoDtos.PagoResponse;
import com.sanaesencia.pago.service.PagoService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    // Crea una transacción de pago para una reserva.
    @PostMapping
    public ResponseEntity<InicioResponse> crear(@Valid @RequestBody CrearPago request) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    // Obtiene un pago mediante su identificador.
    @GetMapping("/{id}")
    public PagoResponse obtener(@PathVariable Long id) {
        return service.dto(service.obtener(id));
    }

    // Lista los pagos asociados a una reserva.
    @GetMapping
    public List<PagoResponse> listar(@RequestParam Long reservaId) {
        return service.listar(reservaId);
    }


    
// Procesa los retornos normales e interrumpidos de Webpay.
@RequestMapping(value = "/webpay/retorno", method = {RequestMethod.GET, RequestMethod.POST})
public ResponseEntity<String> retorno(@RequestParam Map<String, String> params) throws Exception {
    String token = params.get("token_ws");

    // Retorno normal: Webpay entrega el token de la transacción.
    if (token != null && !token.isBlank()) {
        PagoResponse pago = service.confirmar(token);

        return ResponseEntity.ok(
                "Estado del pago: " + pago.estado()
                + ". Vuelve a SanaEsencia para consultar tu reserva."
        );
    }

    // Retorno interrumpido: Webpay entrega los datos TBK.
    String tbkToken = params.get("TBK_TOKEN");
    String ordenCompra = params.get("TBK_ORDEN_COMPRA");
    String idSesion = params.get("TBK_ID_SESION");

    if (tbkToken != null && !tbkToken.isBlank()
            && ordenCompra != null && !ordenCompra.isBlank()
            && idSesion != null && !idSesion.isBlank()) {

        PagoResponse pago = service.validarRetornoInterrumpido(
                tbkToken, ordenCompra, idSesion
        );

        return ResponseEntity.ok(
                "Estado del pago: " + pago.estado()
                + ". Vuelve a SanaEsencia para consultar tu reserva."
        );
    }

    return ResponseEntity.badRequest()
            .body("No se pudo identificar el retorno de Webpay.");
}



    // Solicita la devolución de un pago.
    @PostMapping("/{id}/devoluciones")
    public ResponseEntity<DevolucionResponse> devolver(@PathVariable Long id) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.devolver(id));
    }

    // Lista las devoluciones asociadas a un pago.
    @GetMapping("/{id}/devoluciones")
    public List<DevolucionResponse> devoluciones(@PathVariable Long id) {
        return service.devoluciones(id);
    }
}
