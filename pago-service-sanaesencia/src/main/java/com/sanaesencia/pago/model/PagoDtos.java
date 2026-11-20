package com.sanaesencia.pago.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public final class PagoDtos {

    private PagoDtos() {}

    // Solo puede llamarlo reserva-service despues de validar reserva y usuario.
    public record CrearPago(@NotNull @Positive Long reservaId,
                            @NotNull @Positive Long usuarioId) {}

    public record PagoResponse(Long id, Long reservaId, Long usuarioId,
                               Integer monto, EstadoPago estado, String ordenCompra) {}

    public record InicioResponse(PagoResponse pago, String url, String token) {}

    public record DevolucionResponse(Long id, Long pagoId, Integer monto,
                                    EstadoDevolucion estado, String tipo) {}
}
