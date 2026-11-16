
package com.sanaesencia.reserva.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ServicioClient {

    private final RestClient client;

    public ServicioClient(
            RestClient.Builder builder,
            @Value("${services.servicio-url}") String url
    ) {
        this.client = builder.baseUrl(url).build();
    }

    public ServicioData obtener(Long id, String authorizationHeader) {
        try {
            return client.get()
                    .uri("/api/servicios/{id}", id)
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .retrieve()
                    .body(ServicioData.class);

        } catch (RestClientException exception) {
            throw new IllegalArgumentException(
                    "No fue posible validar el servicio seleccionado",
                    exception
            );
        }
    }

    public EspecialidadData obtenerEspecialidad(
            Long id,
            String authorizationHeader
    ) {
        try {
            return client.get()
                    .uri("/api/especialidades/{id}", id)
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .retrieve()
                    .body(EspecialidadData.class);

        } catch (RestClientException exception) {
            throw new IllegalArgumentException(
                    "No fue posible validar la especialidad del servicio",
                    exception
            );
        }
    }

    public record EspecialidadData(
            Long id,
            String nombre,
            String descripcion
    ) {}

    public record ServicioData(
            Long id,
            String nombre,
            Long especialidadId,
            Integer duracionMinutos,
            String descripcion
    ) {}
}
