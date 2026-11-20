# SanaEsencia — pago-service (versión ordenada)

Java 21 · Spring Boot 4.1.2 · MySQL · Webpay Plus SDK 6.2.1 · puerto 8085.

## Relaciones JPA

- `Pago` tiene `@OneToMany(mappedBy = "pago", cascade = ALL, orphanRemoval = true)` hacia `Devolucion`.
- `Devolucion` tiene `@ManyToOne(fetch = LAZY)` con `@JoinColumn(name="pago_id")` y FK.
- `reservaId` y `usuarioId` son IDs de otros microservicios: **no** se crean claves foráneas hacia ellos.

## Contrato HTTP (backend a backend)

Todos los endpoints excepto `/api/pagos/webpay/retorno` exigen `X-Internal-Key`.
La clave configurada por defecto es un marcador de posición: la aplicación **no arranca** hasta reemplazarla por una clave de al menos 24 caracteres. Usar la misma clave desde reserva-service.

| Método | Ruta | Función |
|---|---|---|
| POST | `/api/pagos` | Crea transacción con `{ "reservaId": 1, "usuarioId": 1 }` |
| GET | `/api/pagos?reservaId=1` | Consulta pagos por reserva |
| GET | `/api/pagos/{id}` | Consulta un pago |
| GET/POST | `/api/pagos/webpay/retorno` | Webpay confirma pago (público) |
| POST | `/api/pagos/{id}/devoluciones` | Solicita devolución |
| GET | `/api/pagos/{id}/devoluciones` | Consulta solicitudes |

## IMPORTANTE: integración pendiente con reserva-service

Este ZIP es el **microservicio de pagos completo como proyecto**, pero **NO es todavía un sistema de reservas y pagos extremo a extremo**. Antes de habilitar pagos reales:

1. `reserva-service` debe verificar usuario autenticado, estado `PENDIENTE_PAGO`, plazo de 15 minutos y propiedad de reserva antes de llamar `POST /api/pagos`. El cliente Android **no debe** llamar directamente a este POST ni aportar un usuarioId confiable.
2. Después del `commit` de Transbank, el pago queda `APROBADO` en la BD de pagos. Aún falta un mecanismo backend **idempotente y autenticado** que cambie la reserva a `CONFIRMADA`, incluyendo reintentos si falla la comunicación.
3. Al vencer los 15 minutos, reserva-service debe consultar el estado real de Transbank y conciliar antes de cambiar a `EXPIRADA`. **No** basta con consultar el estado local `PENDIENTE` ni con un simple `@Scheduled` que expire sin conciliar.
4. `cancelar()` en reserva-service todavía marca `CANCELADA` directamente. Debe pasar primero a `PENDIENTE_CANCELACION`, solicitar devolución y conciliar su resultado. La aceptación de una solicitud de refund **no garantiza** abono final.
5. Para concurrencia real se necesita un bloqueo transaccional de la agenda por profesional/día, o mecanismo equivalente, antes de verificar solapamientos y crear reservas.
6. Para usar el SDK de producción, cambiar credenciales y entorno; **este proyecto usa TEST**. Revisar contratos del SDK instalado y resultados de reembolso antes de usar dinero real.
7. La URL de retorno debe ser pública, HTTPS y alcanzable desde el navegador/Webpay. No uses `localhost` para un dispositivo externo.
8. Los endpoints backend de pago no deben exponerse libremente en el API Gateway; la clave interna no debe enviarse desde Android. Mantener puerto 8085 cerrado a Internet.

## Estado y precio

Monto fijo: CLP $25.000. Estados de pago: `PENDIENTE`, `APROBADO`, `RECHAZADO`, `DEVOLUCION_PENDIENTE`, `DEVUELTO`, `REVISION_MANUAL`.

## Puesta en marcha

1. Crear base MySQL `sanaesencia_pagos`.
2. Ajustar `application.properties` (host MySQL, URL retorno, clave interna).
3. Ejecutar `mvn clean package` (requiere Maven y conexión a dependencias).
4. Iniciar con `java -jar target/pago-service-1.0.0.jar` o construir la imagen con Dockerfile.

No se ha ejecutado compilación ni pruebas integradas en este entorno.
