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

## Principales decisiones

### Endpoints públicos y autenticados

Con el fin de dar un poco de realismo a la API, se ha realizado una división de los endpoints en públicos (no requieren autenticación) y privados (requieren autenticación):

- Endpoints públicos:
  	- `/test/all`
  	- `/auth/login` 
  	- `/auth/register`
- Endpoints privados:
  	- `/test/user`
  	- `/users/*` Además requiere ser el usuario admin.

Los endpoints escogidos públicos son aquellos en los que el usuario no va a estar aún registrado o logeado (`/auth/login` y `/auth/register`) o por el contrario ha accedido a un punto público de la aplicación, como podría ser una página de bienvenida (`/test/all`).

Los endpoints privados son aquellos que necesitan de autenticación del usuario para acceder a él (`/test/user/`) o bien son endpoints que devuelven información sensible de otros usuarios (`/users/`), o permiten borrar los mismos (`DELETE /users/{id}`)

La autenticación se realiza mediante tokens JWT.

### Admin y usuario

Como se menciona en el punto anterior, para acceder al endpoint `/user/*` es necesario ser el usuario admin. Este usuario es creado al iniciar la aplicación con los siguientes credenciales:

- usuario: `admin`
- contraseña: `admin123`

Con estas credenciales se puede hacer login y así obtener el token para acceder al endpoint `/users/*`, y así poder listar todos los usuarios, obtener usuarios por ID, y borrar usuarios. 

## Tokens JWT

Este proyecto usa JWT (JSON Web Tokens) para autenticar peticiones. A grandes rasgos:

- El servicio de autenticación (`AuthServiceImpl`) genera un token llamando a `jwtService.generateToken(username)` al hacer login o register. En esta implementación el token se genera usando el `username` como subject/claim principal.
- La configuración relevante está en `application.properties`:
	- `JWT_SECRET` — clave secreta usada para firmar los tokens. No dejarla en el repositorio en producción.
	- `JWT_EXPIRATION` — tiempo de expiración en milisegundos.

### Uso

- Para acceder a endpoints protegidos hay que enviar el header HTTP:

	Authorization: Bearer <token>

Ejemplo con curl (suponiendo que ya obtuviste el token al hacer POST `/auth/login`):

```bash
curl -H "Authorization: Bearer eyJhbGciOi..." http://localhost:8080/test/user
```

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

Estos valores están colocados en `src/main/resources/application.properties` para esta demo. En producción, será preciso usar variables de entorno o un vault y una clave segura.

<<<<<<< HEAD
## Tokens JWT

Este proyecto usa JWT (JSON Web Tokens) para autenticar peticiones. A grandes rasgos:

- El servicio de autenticación (`AuthServiceImpl`) genera un token llamando a `jwtService.generateToken(username)` al hacer login o register. En esta implementación el token se genera usando el `username` como subject/claim principal.
- La configuración relevante está en `application.properties`:
	- `JWT_SECRET` — clave secreta usada para firmar los tokens. No dejarla en el repositorio en producción.
	- `JWT_EXPIRATION` — tiempo de expiración en milisegundos.

Estructura y uso
- Un JWT tiene la forma `HEADER.PAYLOAD.SIGNATURE`. El payload incluye claims como `sub` (subject, aquí el username) y `exp` (expiración).
- Para acceder a endpoints protegidos hay que enviar el header HTTP:

	Authorization: Bearer <token>

Ejemplo con curl (suponiendo que ya obtuviste el token al hacer POST `/auth/login`):

```bash
curl -H "Authorization: Bearer eyJhbGciOi..." http://localhost:8080/test/user
```

Decodificación y debug
- Puedes pegar el token en https://jwt.io/ para ver el header y payload (no compartas tu `JWT_SECRET`).

Buenas prácticas y limitaciones de esta demo
- No hay refresh token implementado; los tokens expiran según `JWT_EXPIRATION` y la única forma de obtener uno nuevo es volver a autenticarse.
- En esta demo el token contiene únicamente el username como identificador (subject). En producción conviene añadir claims de roles/authorities y evitar lógica de permisos basada en el username (por ejemplo, comparar `username.equals("admin")`).
- Siempre usar HTTPS en producción para evitar que los tokens sean interceptados.
- No almacenar secretos en el repositorio. Pásalos al contenedor o a un secret manager (por ejemplo, via `-e JWT_SECRET=...` en Docker, variables de entorno en Kubernetes, o un vault).


## Credenciales por defecto
=======
## Ejecución
>>>>>>> 89022a470167a52a494657f9c712f60cf5e589fa

### Compilación y ejecución local

Antes de poder ejecutar el proyecto es necesario instalar [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html).

Usando el wrapper de Maven incluido (recomendado):

```bash
# Compilación
./mvnw clean install 

# Ejecución
./mvnw spring-boot:run
```

La API por defecto estará en `http://localhost:8080/`.

### Despliegue en Docker

Como segunda opción también se puede desplegar un contenedor que compilará el código fuente, generará un archivo `.jar` ejecutable, y lo ejecutará.

Es necesario tener docker instalado en el equipo:

```bash
# Construcción de la imagen docker user-api
sudo docker build -t user-api .

# Ejecución del contenedor
sudo docker run -p 8080:8080 user-api
```

Al igual que en la compilación local, la API estará en `http://localhost:8080/`.

## Ejemplos de curl: `api_test.sh`

Hay un script de pruebas escrito en bash incluido en la raíz del proyecto llamado `api_test.sh` que realiza una serie de llamadas HTTP para comprobar los endpoints principales (registro, login, endpoints públicos y protegidos, operaciones de administración y casos de error).

Requisitos
- En el caso de utilizar Windows, es necesario [WSL](https://learn.microsoft.com/es-es/windows/wsl/install).
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

## Buenas prácticas y limitaciones de esta demo
- No hay refresh token implementado; los tokens expiran según `JWT_EXPIRATION` y la única forma de obtener uno nuevo es volver a autenticarse.
- En esta demo el token contiene únicamente el username como identificador (subject). En producción conviene añadir claims de roles/authorities y evitar lógica de permisos basada en el username (por ejemplo, comparar `username.equals("admin")`).
- Siempre usar HTTPS en producción para evitar que los tokens sean interceptados.
- No almacenar claves en el repositorio.
   Añadir validaciones DTO más completas y manejo de excepciones centralizado (ya existe una base en `exception/`).
- Persistencia H2: para producción configurar una base de datos real y ajustes de JPA/Hibernate.

