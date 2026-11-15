# profesional-service — SanaEsencia
Gestión RESTful de profesionales, especialidades asociadas y modalidades.

- Puerto: `8083`
- Base de datos: `sanaesencia_profesionales`
- Swagger: `http://localhost:8083/swagger-ui.html`

## Endpoints
| Método | Ruta | Uso |
|---|---|---|
| GET | `/api/profesionales` | Listar profesionales |
| GET | `/api/profesionales?especialidadId=1` | Filtrar por especialidad |
| GET | `/api/profesionales/{id}` | Consultar profesional |
| POST | `/api/profesionales` | Crear (ADMIN) |
| PUT | `/api/profesionales/{id}` | Actualizar (ADMIN) |
| DELETE | `/api/profesionales/{id}` | Eliminar (ADMIN) |

`especialidadesIds` son referencias a IDs gestionados por `catalogo-service`. Al ser bases independientes, este servicio no crea una FK MySQL hacia la base del catálogo. La validación cruzada entre servicios queda para una siguiente etapa de integración.
