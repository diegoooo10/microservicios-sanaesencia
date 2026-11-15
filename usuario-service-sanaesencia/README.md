# SanaEsencia — usuario-service

Microservicio de usuarios con Spring Boot, Java 21, MySQL, Spring Security y JWT.

## Requisitos

- JDK 21
- Maven 3.9+
- MySQL (por ejemplo, Laragon)
- IntelliJ IDEA o Spring Tool Suite

## Base de datos

La aplicación intenta crear la base de datos si no existe:

`sanaesencia_usuarios`

Configuración por defecto en `application.properties`:
- URL: `jdbc:mysql://localhost:3306/sanaesencia_usuarios`
- Usuario: `root`
- Contraseña: vacía

Ajusta `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` como variables de entorno si tu instalación usa otros valores.

## Ejecutar

Desde la carpeta del proyecto:

```bash
mvn spring-boot:run
```

La aplicación utiliza el puerto `8081`.

## Swagger

Abre:

`http://localhost:8081/swagger-ui.html`

## Endpoints

| Método | Endpoint | Acceso |
|---|---|---|
| POST | `/api/usuarios/registro` | Público |
| POST | `/api/usuarios/login` | Público |
| GET | `/api/usuarios/perfil` | Autenticado |
| PUT | `/api/usuarios/perfil` | Autenticado |
| GET | `/api/usuarios` | ADMIN |
| GET | `/api/usuarios/{id}` | ADMIN |
| DELETE | `/api/usuarios/{id}` | ADMIN |

## Ejemplo de registro

```json
{
  "nombre": "Ana",
  "apellido": "Pérez",
  "telefono": "+56912345678",
  "fechaNacimiento": "1995-04-20",
  "ciudad": "Villa Alemana",
  "correo": "ana@example.com",
  "password": "ClaveSegura123"
}
```

`ciudad` es opcional. El registro público siempre asigna el rol `CLIENTE`; nunca se acepta el rol desde el cuerpo de la solicitud.

## Ejemplo de inicio de sesión

```json
{
  "correo": "ana@example.com",
  "password": "ClaveSegura123"
}
```

La respuesta incluye un token de tipo `Bearer` y los datos públicos del usuario.

Para consumir endpoints protegidos, enviar:

`Authorization: Bearer <token>`

## Crear un administrador para pruebas

Por seguridad, el endpoint público de registro no permite escoger el rol. Registra primero un usuario normal y luego, desde MySQL, cambia su rol:

```sql
USE sanaesencia_usuarios;
UPDATE usuarios SET rol = 'ADMIN' WHERE correo = 'ana@example.com';
```

Cierra sesión e inicia sesión nuevamente para obtener un token que incluya el rol actualizado.

## Seguridad y configuración

- Las contraseñas se almacenan con BCrypt.
- La API es stateless y no utiliza sesiones HTTP.
- El JWT se firma con una clave HMAC configurada mediante `JWT_SECRET`.
- Cambia el secreto de ejemplo antes de desplegar o compartir el servicio.
- La duración del token se configura mediante `JWT_EXPIRATION_MS` (por defecto, 24 horas).

## Nota de integración Android

Los campos `LocalDate` se intercambian como texto ISO `yyyy-MM-dd`. El frontend debe enviar el token JWT en el encabezado `Authorization` para las rutas protegidas.
