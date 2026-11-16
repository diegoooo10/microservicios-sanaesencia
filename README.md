# SanaEsencia Backend completo - demostración

Incluye cuatro microservicios independientes y API Gateway:
- usuario-service-sanaesencia (8081)
- servicio-service-sanaesencia (8082)
- profesional-service-sanaesencia (8083)
- reserva-service-sanaesencia (8084)
- api-gateway-sanaesencia (8080)

Stack: Java 21, Spring Boot 4.1.2, Maven, MySQL 8.4, Spring Security, JWT, Springdoc OpenAPI.

## Ejecutar
Desde la carpeta que contiene `compose.yaml`:
```powershell
docker compose up --build -d
docker compose ps
```

Detener:
```powershell
docker compose down
```
Los datos persisten en volúmenes. Para reiniciar las bases desde cero (BORRA LOS DATOS):
```powershell
docker compose down -v
```

## Swagger
- Usuarios: http://localhost:8081/swagger-ui/index.html
- Servicios: http://localhost:8082/swagger-ui/index.html
- Profesionales: http://localhost:8083/swagger-ui/index.html
- Reservas: http://localhost:8084/swagger-ui/index.html

## Gateway
El gateway enruta:
- `/api/usuarios/**`
- `/api/especialidades/**`
- `/api/servicios/**`
- `/api/profesionales/**`
- `/api/reservas/**`

## Endpoints principales
### Usuarios
- POST `/api/usuarios/registro`
- POST `/api/usuarios/login`
- GET `/api/usuarios/perfil` (CLIENTE)
- PATCH `/api/usuarios/perfil` (CLIENTE)

Registro JSON:
```json
{
  "nombre": "Ana",
  "apellido": "Pérez",
  "correo": "ana@example.com",
  "run": "12345678-9",
  "fechaNacimiento": "1995-05-10",
  "password": "ClaveSegura123"
}
```

Login JSON:
```json
{
  "correo": "ana@example.com",
  "password": "ClaveSegura123"
}
```

### Servicios
- GET `/api/especialidades`
- GET `/api/especialidades/{id}`
- GET `/api/servicios`
- GET `/api/servicios?especialidadId=1`
- GET `/api/servicios/{id}`

### Profesionales
- GET `/api/profesionales`
- GET `/api/profesionales?especialidadId=1`
- GET `/api/profesionales/{id}`

### Reservas
Todas requieren `Authorization: Bearer <token>`.
- POST `/api/reservas`
- GET `/api/reservas`
- GET `/api/reservas/{id}`
- PATCH `/api/reservas/{id}/cancelacion`

Crear reserva JSON:
```json
{
  "profesionalId": 1,
  "servicioId": 1,
  "fecha": "2027-01-15",
  "hora": "10:00:00",
  "modalidad": "Online"
}
```
La duración, nombre de la especialidad, nombre del profesional y nombre del servicio se obtienen de los otros microservicios, no del cliente. Las respuestas de reserva usan DTO y no exponen el correo interno del usuario.

## Datos de demostración
El servicio inicializa especialidades y servicios de ejemplo.
Profesionales crea dos profesionales asociados a especialidad 1 y 2.
En un entorno limpio los IDs iniciales son 1 y 2.

## Seguridad
Todos los microservicios comparten el mismo secreto JWT de demostración definido en `application.properties` y en Compose. El secreto es público dentro de este paquete y SOLO sirve para pruebas locales. Sustituirlo por un secreto privado antes de desplegar.
La contraseña de usuario se almacena con BCrypt. No se expone en las respuestas.

## Decisiones y límites conocidos
- La app está orientada a clientes: no se exponen endpoints administrativos para escritura de servicios ni profesionales.
- Los GET de servicios y profesionales son públicos para permitir explorar la oferta antes de iniciar sesión.
- Las reservas se vinculan al correo extraído del JWT; el cliente solo puede consultar/cancelar sus propias reservas.
- Reserva consulta Servicios y Profesionales por HTTP antes de persistir, y valida especialidad, modalidad y estado de agenda.
- Los horarios de atención y disponibilidad granular por bloques todavía no están modelados; se evita doble reserva por solapamiento entre reservas persistidas, pero no se genera un calendario completo de disponibilidad.
- La cancelación requiere 12 horas de anticipación.
- `ddl-auto=update` es apropiado para esta demo; para producción debe migrarse a Flyway/Liquibase y endurecer credenciales, CORS, rate limiting, logs y políticas de despliegue.
- El modelo `ProfesionalResponse` mantiene `especialidadesIds`; el modelo `ServicioResponse` usa `especialidadId`, `duracionMinutos`.
