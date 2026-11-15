# catalogo-service — SanaEsencia
Catálogo RESTful de especialidades y servicios terapéuticos.

- Puerto: `8082`
- Base de datos: `sanaesencia_catalogo`
- Swagger: `http://localhost:8082/swagger-ui.html`

## Endpoints
| Método | Ruta | Uso |
|---|---|---|
| GET | `/api/especialidades` | Listar especialidades |
| GET | `/api/especialidades/{id}` | Consultar especialidad |
| POST | `/api/especialidades` | Crear especialidad (ADMIN) |
| PUT | `/api/especialidades/{id}` | Actualizar especialidad (ADMIN) |
| DELETE | `/api/especialidades/{id}` | Eliminar si no tiene servicios (ADMIN) |
| GET | `/api/servicios?especialidadId=1` | Listar/filtrar servicios |
| GET | `/api/servicios/{id}` | Consultar servicio |
| POST | `/api/servicios` | Crear servicio (ADMIN) |
| PUT | `/api/servicios/{id}` | Actualizar servicio (ADMIN) |
| DELETE | `/api/servicios/{id}` | Eliminar servicio (ADMIN) |

GET es público para permitir que la app consulte el catálogo. Las operaciones de escritura requieren JWT con rol ADMIN.
La referencia `especialidadId` es un identificador de otro recurso dentro del mismo servicio, por lo que puede validarse localmente.
