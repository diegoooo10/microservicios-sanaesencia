# Microservicios SanaEsencia

Repositorio que reúne los microservicios backend de **SanaEsencia**, una
aplicación Android desarrollada con Kotlin y Jetpack Compose.

La solución está organizada como cuatro microservicios independientes,
desarrollados con Spring Boot, que exponen APIs RESTful y utilizan bases
de datos MySQL separadas.

## Arquitectura

  -----------------------------------------------------------------------------------------
  Microservicio                       Responsabilidad         Base de datos
  ----------------------------------- ----------------------- -----------------------------
  `usuario-service-sanaesencia`       Registro, inicio de     `sanaesencia_usuarios`
                                      sesión, gestión de      
                                      usuarios y              
                                      autenticación mediante  
                                      JWT.                    

  `catalogo-service-sanaesencia`      Administración y        `sanaesencia_catalogo`
                                      consulta de             
                                      especialidades y        
                                      servicios.              

  `profesional-service-sanaesencia`   Gestión y consulta de   `sanaesencia_profesionales`
                                      profesionales.          

  `reserva-service-sanaesencia`       Gestión de reservas y   `sanaesencia_reservas`
                                      sus estados.            
  -----------------------------------------------------------------------------------------

Cada microservicio mantiene su propio proyecto Maven, configuración,
capa de persistencia y base de datos. Esta separación permite
desarrollar y ejecutar los servicios de manera independiente.

## Tecnologías

-   Java 21
-   Spring Boot 4.1.2
-   Apache Maven
-   Spring Web
-   Spring Data JPA
-   Spring Security
-   Spring Validation
-   MySQL
-   JWT (JJWT 0.12.6)
-   Springdoc OpenAPI / Swagger UI (3.0.1)

## Estructura del repositorio

``` text
Microservicios-SanaEsencia/
├── usuario-service-sanaesencia/
├── catalogo-service-sanaesencia/
├── profesional-service-sanaesencia/
├── reserva-service-sanaesencia/
├── .gitignore
└── README.md
```

## Requisitos

-   JDK 21.
-   Apache Maven instalado y disponible en el `PATH`.
-   MySQL en ejecución (por ejemplo, mediante Laragon).
-   La base de datos correspondiente creada en MySQL, salvo que la
    configuración local del servicio esté preparada para crearla
    automáticamente.

## Configuración local

Cada microservicio tiene su propio archivo
`src/main/resources/application.properties`.

Antes de ejecutarlo:

1.  Revisa el puerto configurado en el archivo del servicio.
2.  Configura la URL JDBC para su base de datos.
3.  Configura el usuario y la contraseña de MySQL mediante variables de
    entorno o un mecanismo local que no se suba al repositorio.
4.  Configura la clave secreta JWT de forma segura y coherente entre los
    servicios que validan esos tokens.

**Seguridad:** no publiques contraseñas, claves JWT ni credenciales
reales. Evita guardar secretos directamente en archivos versionados.
Para valores locales, utiliza variables de entorno o un archivo local
excluido por `.gitignore`.

## Compilar y ejecutar

Los comandos se ejecutan desde la carpeta del microservicio que se desea
trabajar.

### Compilar

``` powershell
mvn clean compile
```

Una compilación correcta finaliza con `BUILD SUCCESS`. Esto verifica la
compilación, pero no garantiza por sí solo que la conexión a MySQL, la
autenticación o los endpoints funcionen.

### Ejecutar

``` powershell
mvn spring-boot:run
```

También se puede abrir el proyecto individualmente en un IDE y ejecutar
su clase principal de Spring Boot.

### Swagger UI

Cuando el servicio esté iniciado, abre en el navegador:

``` text
http://localhost:<PUERTO>/swagger-ui/index.html
```

Reemplaza `<PUERTO>` por el puerto configurado en el
`application.properties` del microservicio.

## Autenticación

El servicio de usuarios implementa registro e inicio de sesión y genera
tokens JWT. Los microservicios protegidos utilizan Spring Security y un
filtro JWT para validar las solicitudes autenticadas.

Para acceder a un endpoint protegido, el cliente debe enviar el token en
el encabezado HTTP:

``` http
Authorization: Bearer <TOKEN>
```

Los permisos concretos dependen de la configuración de seguridad y de
los roles definidos en cada servicio.

## Integración con la aplicación Android

La aplicación cliente está desarrollada con Kotlin y Jetpack Compose. La
comunicación con los microservicios se realizará mediante solicitudes
HTTP, utilizando Retrofit en la capa de datos.

Al ejecutar la aplicación en un dispositivo físico, la URL base debe
apuntar a la dirección IP local del computador donde se ejecutan los
servicios, junto con el puerto correspondiente. `localhost` desde el
teléfono hace referencia al propio teléfono, no al computador.

## Organización de ramas

  -------------------------------------------------------------------------
  Rama                                Contenido
  ----------------------------------- -------------------------------------
  `main`                              Contiene los cuatro microservicios y
                                      representa la versión integrada.

  `usuario-service`                   Contiene únicamente
                                      `usuario-service-sanaesencia/`.

  `catalogo-service`                  Contiene únicamente
                                      `catalogo-service-sanaesencia/`.

  `profesional-service`               Contiene únicamente
                                      `profesional-service-sanaesencia/`.

  `reserva-service`                   Contiene únicamente
                                      `reserva-service-sanaesencia/`.
  -------------------------------------------------------------------------

### Flujo de trabajo

1.  Cambiar a la rama del microservicio que se va a desarrollar.
2.  Realizar y probar los cambios en ese servicio.
3.  Crear commits descriptivos y subirlos a su rama remota.
4.  Cuando los cambios estén revisados, integrarlos en `main` mediante
    merge o pull request.
5.  Mantener `main` como la versión integrada y estable del proyecto.

Ejemplo:

``` powershell
git switch usuario-service
# Realizar cambios en usuario-service-sanaesencia/
git add .
git commit -m "Describe el cambio realizado"
git push
```

Para integrar cambios, primero se debe revisar la rama y luego realizar
el merge o pull request correspondiente. No se deben mezclar
directamente las ramas de desarrollo entre sí.

## Estado del proyecto

Los cuatro proyectos han superado la compilación con Maven y se ha
comprobado el inicio de los servicios y la creación de sus bases de
datos locales. El registro de un cliente en `usuario-service` también se
probó mediante Swagger.

La integración completa entre los microservicios y la aplicación
Android, así como las pruebas funcionales exhaustivas de todos los
endpoints y de la comunicación entre servicios, forman parte de las
siguientes etapas de desarrollo.

------------------------------------------------------------------------

**SanaEsencia --- Backend de microservicios**
