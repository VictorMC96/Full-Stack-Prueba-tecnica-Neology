# Neology Parking

Prueba técnica Full Stack para la gestión de acceso de vehículos a un estacionamiento.

El proyecto está dividido en:

- `backend/`: API REST con Spring Boot
- `frontend/`: aplicación web con Angular

## Requisitos

Para ejecutar el proyecto se necesita:

- Java 17 o superior
- Maven
- Node.js
- npm
- Angular CLI

## Levantar el backend

Desde la carpeta `backend/`:

### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

### Linux / macOS

```bash
./mvnw spring-boot:run
```

El backend queda disponible en:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

## Levantar el frontend

Desde la carpeta `frontend/`:

Instalar dependencias:

```bash
npm install
```

Iniciar la aplicación:

```bash
ng serve
```

El frontend queda disponible en:

```text
http://localhost:4200
```

El backend debe estar ejecutándose en el puerto `8080`.

## Ejecutar pruebas

### Backend

Desde `backend/`:

Windows:

```powershell
.\mvnw.cmd clean test
```

Linux / macOS:

```bash
./mvnw clean test
```

### Frontend

Desde `frontend/`:

```bash
ng test --watch=false
```

## Tecnologías principales

Backend:

- Java 17
- Spring Boot 3
- Spring Data JPA / Hibernate
- H2
- JUnit 5
- Mockito

Frontend:

- Angular
- Angular Material
- Reactive Forms
- SCSS
- Vitest
