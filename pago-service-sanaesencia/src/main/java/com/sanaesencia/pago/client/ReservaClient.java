
package com.sanaesencia.pago.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ReservaClient {

    private final RestClient restClient;
    private final String internalKey;

    public ReservaClient(
            @Value("${services.reserva-url}") String reservaUrl,
            @Value("${app.internal.api-key}") String internalKey
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(reservaUrl)
                .build();

        this.internalKey = internalKey;
    }

    public void confirmarPago(Long reservaId) {
        restClient.patch()
                .uri("/api/interno/reservas/{id}/confirmacion", reservaId)
                .header("X-Internal-Key", internalKey)
                .retrieve()
                .toBodilessEntity();
    }


    public void noConcretarPago(Long reservaId) {
        restClient.patch()
                .uri("/api/interno/reservas/{id}/no-concretada", reservaId)
                .header("X-Internal-Key", internalKey)
                .retrieve()
                .toBodilessEntity();
    }

}
