# SanaEsencia — Microservicios adicionales

Este paquete contiene tres proyectos Spring Boot independientes:

| Proyecto | Puerto | Base de datos |
|---|---:|---|
| catalogo-service | 8082 | sanaesencia_catalogo |
| profesional-service | 8083 | sanaesencia_profesionales |
| reserva-service | 8084 | sanaesencia_reservas |

El `usuario-service` se entrega por separado. Todos los servicios esperan JWT HMAC con el mismo secreto compartido configurado por `JWT_SECRET`, compatible con el secreto local de desarrollo del `usuario-service`.

## Orden sugerido
1. Levantar MySQL / Laragon.
2. Ejecutar `usuario-service` (puerto 8081).
3. Ejecutar `catalogo-service` (8082).
4. Ejecutar `profesional-service` (8083).
5. Ejecutar `reserva-service` (8084).

Cada proyecto tiene su propio `pom.xml` y README. Abrir cada carpeta como proyecto Maven independiente.

## Nota sobre REST y límites del MVP
Los recursos se exponen con sustantivos plurales, GET/POST/PUT/PATCH/DELETE y códigos HTTP convencionales. Las lecturas de catálogos son públicas; las escrituras requieren rol ADMIN. Reservas requiere JWT para acciones de cliente y administración.

La arquitectura de microservicios es una decisión de diseño del equipo, no una exigencia textual confirmada por el cliente en el documento de requisitos preliminar. Los requisitos funcionales y las reglas de cancelación están sujetos a validación. Utilizar solo datos ficticios o anonimizados en la demo.

## No incluido en este paquete
- Productos/tienda: la interfaz Android actual no contiene la tienda; se puede crear `producto-service` si se reincorpora ese alcance.
- Notificaciones: envío de correo/WhatsApp/push requiere definir proveedor, credenciales, consentimiento y eventos.
- Agenda y boxes: el modelo completo de turnos laborales, disponibilidad y ocho boxes necesita reglas y datos del centro.
- Integración MEDIlink: no se asume API disponible ni permisos de integración.
- Historial clínico o datos sensibles de salud: fuera del alcance de esta demo.
