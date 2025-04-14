# Star Wars People API



API que proporciona información sobre personajes del universo Star Wars.

> 🔧 Actualmente solo se encuentra implementado el módulo de **People**. Los módulos de Films, Starships y Vehicles están pensados con una estructura similar, pero aún no han sido desarrollados.

---

## 🧰 Tecnologías utilizadas

- Java 8
- Spring Boot
- Maven (con wrapper incluido)
- PostgreSQL
- Flyway (migraciones de base de datos)
- Spring Security (con manejo de sesiones y formularios HTML)
- Caffeine (para caché)
- JUnit, Mockito, Spring Test (tests unitarios y de integración)
- Swagger (documentación de la API)
- Lombok

---

## 🚀 Cómo ejecutar el proyecto

### ✅ Opción 1: Usando Docker (recomendado)

#### Requisitos
- Tener [Docker](https://www.docker.com/products/docker-desktop/) y [Docker Compose](https://docs.docker.com/compose/install/) instalados y configurados.
- Asegurarse de que **Docker Desktop esté en ejecución** antes de correr los comandos.

#### Comando
```bash
docker-compose up --build
```

Esto levantará:
- La aplicación Java
- Una base de datos PostgreSQL

La aplicación estará disponible en `http://localhost:8080`

### 🧪 Opción 2: Sin Docker

#### Requisitos
- Java 8
- PostgreSQL
- Maven (o usar el wrapper `./mvnw` incluido)

#### Pasos
1. Asegurate de tener PostgreSQL corriendo y con una base de datos llamada `starwars`.
2. Ejecutá la aplicación con:

```bash
./mvnw spring-boot:run
```

La app quedará disponible en `http://localhost:8080`

---

## 🔒 Seguridad

La autenticación está gestionada con **Spring Security**, utilizando formularios HTML y sesiones.

- Los endpoints públicos son:
  - `/auth/**` (login, logout, register)
  - `/swagger-ui.html`, `/api-docs/**`
- Rutas protegidas requieren autenticación

🧪 Usuario de prueba cargado:
- Email: `admin@mail.com`
- Contraseña: `adminpass`

---

## 📄 Swagger UI

Documentación interactiva disponible en:
```
http://localhost:8080/swagger-ui/index.html
```

---

## 🖥️ Interfaz web

Además de los endpoints REST, la aplicación cuenta con vistas accesibles desde el navegador:

`http://localhost:8080`

Estas vistas usan formularios HTML y sesiones para el manejo de autenticación.

---

## 🧪 Tests

Incluye:
- Tests unitarios y de integración
- Frameworks utilizados: `JUnit`, `Mockito`, `Spring Test`

Para correr los tests:
```bash
./mvnw test
```






