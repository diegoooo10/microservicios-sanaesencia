
package com.sanaesencia.reserva.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class PagoClient {

    private final RestClient client;
    private final String internalKey;

    public PagoClient(
            @Value("${services.pago-url}") String url,
            @Value("${app.internal.key}") String internalKey
    ) {
        this.client = RestClient.builder()
                .baseUrl(url)
                .build();
        this.internalKey = internalKey;
    }

    public InicioResponse crear(Long reservaId, Long usuarioId) {
        try {
            InicioResponse response = client.post()
                    .uri("/api/pagos")
                    .header("X-Internal-Key", internalKey)
                    .body(new CrearPago(reservaId, usuarioId))
                    .retrieve()
                    .body(InicioResponse.class);

            if (response == null
                    || response.url() == null
                    || response.token() == null) {
                throw new IllegalStateException(
                        "El servicio de pagos devolvió una respuesta incompleta"
                );
            }

            return response;

        } catch (RestClientException exception) {
            throw new IllegalStateException(
                    "No fue posible iniciar el pago de la reserva",
                    exception
            );
        }
    }

    public record CrearPago(Long reservaId, Long usuarioId) {}

    public record InicioResponse(
            PagoResponse pago,
            String url,
            String token
    ) {}

    public record PagoResponse(
            Long id,
            Long reservaId,
            Long usuarioId,
            Integer monto,
            String estado,
            String ordenCompra
    ) {}
}
