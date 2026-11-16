
package com.sanaesencia.reserva.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ProfesionalClient {

    private final RestClient client;

    public ProfesionalClient(
            RestClient.Builder builder,
            @Value("${services.profesional-url}") String url
    ) {
        this.client = builder.baseUrl(url).build();
    }

    public ProfesionalData obtener(Long id, String authorizationHeader) {
        try {
            return client.get()
                    .uri("/api/profesionales/{id}", id)
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .retrieve()
                    .body(ProfesionalData.class);

        } catch (RestClientException exception) {
            throw new IllegalArgumentException(
                    "No fue posible validar el profesional seleccionado",
                    exception
            );
        }
    }

    public record ProfesionalData(
            Long id,
            String nombre,
            String tituloProfesional,
            java.util.List<Long> especialidadesIds,
            java.util.List<String> modalidades,
            String estadoAgenda
    ) {}
}
