# reserva-service — SanaEsencia
Agendamiento RESTful, consulta de reservas y cancelación conforme a la regla preliminar de 12 horas.

- Puerto: `8084`
- Base de datos: `sanaesencia_reservas`
- Swagger: `http://localhost:8084/swagger-ui.html`

## Endpoints
| Método | Ruta | Acceso |
|---|---|---|
| POST | `/api/reservas` | CLIENTE autenticado |
| GET | `/api/reservas/mis-reservas` | CLIENTE autenticado |
| GET | `/api/reservas/mis-reservas/{id}` | Propietario |
| PATCH | `/api/reservas/mis-reservas/{id}/cancelacion` | Propietario |
| GET | `/api/reservas` | ADMIN |

## Reglas incluidas
- El cliente autenticado se identifica por el subject (correo) del JWT; no se acepta un correo de usuario desde el body.
- Se impide reservar en el pasado.
- Se impiden cruces horarios para el mismo profesional, considerando la duración.
- La cancelación exige al menos 12 horas antes del inicio.
- La reserva cancelada se conserva en el historial.

## Alcance y limitaciones deliberadas
El proyecto usa `profesionalId` y `servicioId` como referencias a otros microservicios. No crea claves foráneas entre bases de datos.
Por ahora la validación de que esos IDs existan, la agenda laboral del profesional, la asignación de los ocho boxes y la notificación al profesional/administración no se implementan: requieren comunicación entre microservicios y reglas confirmadas. La detección de conflictos actual cubre reservas existentes del mismo profesional, no una agenda completa ni concurrencia distribuida.
