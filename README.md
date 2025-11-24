# user-api

API REST simple en Java (Spring Boot) para gestión de usuarios con autenticación JWT.

## Resumen

Proyecto demo construido con Spring Boot 3 (Java 17). Usa H2 en memoria para persistencia, JWT para autenticación y Spring Security para proteger endpoints. Incluye registro/login, endpoints públicos y endpoints protegidos para administración de usuarios.

## Tecnologías

- Java 17
- Spring Boot 3.5.7
- Spring Web, Spring Data JPA, Spring Security
- H2 (base de datos en memoria)
- JJWT (io.jsonwebtoken) para tokens JWT
- Maven (con `mvnw` wrapper)

## Estructura del proyecto

Raíz: `src/main/java/com/example/user_api`

- `controller/` — Controladores REST
	- `AuthController.java` — `/auth` (login y register)
	- `AdminController.java` — `/users` (endpoints CRUD de usuarios accesibles solo por admin)
	- `TestController.java` — `/test` (endpoints de ejemplo: público y para usuarios autenticados)

- `dto/` — Clases DTO para peticiones y respuestas (por ejemplo `LoginRequest`, `RegisterRequest`, `UserResponse`).

- `model/` — Entidades JPA
	- `User.java` — entidad `app_user` con fields: `id (UUID)`, `username`, `email`, `password`, `creationDate`.

- `repository/` — Repositorios Spring Data JPA
	- `UserRepository.java` — búsquedas por username/email y métodos `existsBy...`.

- `service/` — Interfaces de servicio
	- `AuthService`, `UserService`

- `service/impl/` — Implementaciones
	- `AuthServiceImpl.java` — lógica de login y registro (valida contraseñas, guarda usuario, genera token JWT)
	- `UserServiceImpl.java` — obtener todos los usuarios, obtener por id, borrar usuario

- `security/` — Lógica de seguridad
	- `JwtService.java` — generar/validar/extract username del token
	- `JwtAuthFilter.java` — filtro que extrae token del header `Authorization: Bearer <token>` e inyecta la autenticación en el context
	- `SecurityConfig.java` — configuración de Spring Security y reglas de acceso

- `exception/` — Excepciones personalizadas y manejador global

- `resources/` — Recursos
	- `application.properties` — configuración (ej., `JWT_SECRET`, `JWT_EXPIRATION`)

## Endpoints

- POST `/auth/login` — Body: `LoginRequest` (username, password). Responde `UserResponse` con token JWT en el último campo.
- POST `/auth/register` — Body: `RegisterRequest` (username, email, password). Crea usuario y devuelve `UserResponse` con token.
- GET `/test/all` — público
- GET `/test/user` — requiere autenticación (token)
- GET `/users` — requiere ser admin (ver `SecurityConfig`): lista de usuarios
- GET `/users/{id}` — requiere admin: obtiene usuario por id
- DELETE `/users/{id}` — requiere admin: borra usuario

Autenticación: incluir header `Authorization: Bearer <token>` para endpoints protegidos.

## Variables de entorno / configuración

- `JWT_SECRET` — clave secreta para firmar tokens (en `application.properties` en este repo está definida una clave de ejemplo).
- `JWT_EXPIRATION` — tiempo en ms de expiración del token (por ejemplo `3600000` = 1 hora).

Estos valores están colocados en `src/main/resources/application.properties` para esta demo. En producción, usar variables de entorno o un vault y una clave segura.

## Credenciales por defecto

Al iniciar la aplicación, el `CommandLineRunner` crea un usuario administrador si no existe:

- usuario: `admin`
- contraseña: `admin123`

Usar estas credenciales para obtener un token y probar los endpoints `/users/**`.

## Cómo compilar y ejecutar

Antes de poder ejecutar el proyecto es necesario instalar [Java 17]([https://ejemplo.com](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html).

Usando el wrapper de Maven incluido (recomendado):

```bash
# compilar
./mvnw clean install 

# ejecutar con Spring Boot
./mvnw spring-boot:run
```

La API por defecto estará en `http://localhost:8080/`.

## Ejemplos de curl: `api_test.sh`

Hay un script de pruebas incluido en la raíz del proyecto llamado `api_test.sh` que realiza una serie de llamadas HTTP para comprobar los endpoints principales (registro, login, endpoints públicos y protegidos, operaciones de administración y casos de error).

Requisitos
- `curl` (para hacer las peticiones HTTP)
- `jq` (opcional, para formatear JSON en la salida; si no está instalado verás JSON crudo)
- La aplicación corriendo en `http://localhost:8080/` (o modificar el script para apuntar a otra URL)

Hacer ejecutable y ejecutar

```bash
chmod +x api_test.sh
./api_test.sh
```

Qué hace `api_test.sh` (resumen)
- Define `BASE_URL` (por defecto `http://localhost:8080`).
- Registra dos usuarios normales con `/auth/register`:
	- `antonito` (password: `12345678`)
	- `maria` (password: `abcdefgh`)
- Hace login con `antonito` para obtener `USER_TOKEN` y lo muestra.
- Llama al endpoint protegido `/test/user` usando `USER_TOKEN` para comprobar acceso autenticado.
- Llama al endpoint público `/test/all`.
- Hace login con las credenciales de admin (`admin` / `admin123`) para obtener `ADMIN_TOKEN`.
- Lista todos los usuarios con `/users` usando `ADMIN_TOKEN`.
- Recupera el `id` del usuario `antonito` (hace un login y extrae `.id`) y pide `/users/{id}` con `ADMIN_TOKEN`.
- Elimina ese usuario con `DELETE /users/{id}` usando `ADMIN_TOKEN` y vuelve a listar usuarios para mostrar el cambio.
- Ejecuta varios casos de error para comprobar excepciones:
	- login con usuario inexistente
	- login con contraseña incorrecta
	- intento de acceder a `/users` con el token de un usuario normal (permiso insuficiente)
	- comprobación de cabeceras en `/test/user` sin token

Salida y formato
- El script hace uso de `jq` para imprimir JSON con formato; si `jq` no está disponible, la salida será JSON sin formatear.

## Postman
También se incluye el archivo postman.html que contiene una collection con todos los tipos de peticiones que se le pueden realizar a la aplicación.

Nota:
Para utilizar tokens en las peticiones se incluyen en la parte de Auth, y el tipo de autenticación a elegir será Bearer Token. 

## Notas de seguridad y mejoras sugeridas

- No usar la clave `JWT_SECRET` que aparece en el `application.properties` en producción.
- Añadir roles/authorities más robustos en lugar de comparar `username.equals("admin")`.
- Añadir validaciones DTO más completas y manejo de excepciones centralizado (ya existe una base en `exception/`).
- Persistencia H2: para producción configurar una base de datos real y ajustes de JPA/Hibernate.


