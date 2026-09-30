# Gestión de Acceso Vehicular — instrucciones para agentes

Sistema web para gestionar el acceso de vehículos a un estacionamiento: backend Spring Boot en
`backend/`, frontend Angular en `frontend/`. Prueba técnica Full Stack para Neology.

## Commands

Shell: Git Bash. Cada comando se ejecuta desde la raíz del proyecto.

| Task | Command |
|---|---|
| Backend — arrancar | `(cd backend && ./mvnw spring-boot:run)` — http://localhost:8080 |
| Backend — puerta completa | `(cd backend && ./mvnw -q -B -ntp verify)` |
| Backend — formatear | `(cd backend && ./mvnw -q -B -ntp spotless:apply)` |
| Frontend — instalar | `(cd frontend && npm ci)` |
| Frontend — arrancar | `(cd frontend && npm start)` — http://localhost:4200 |
| Frontend — pruebas | `(cd frontend && npm run test:ci)` |
| Frontend — formato | `(cd frontend && npm run format:check)` |
| Frontend — build | `(cd frontend && npm run build)` |

**Gate:** `(cd backend && ./mvnw -q -B -ntp verify)` y `(cd frontend && npm run format:check && npm run test:ci && npm run build)`
deben salir con 0 antes de dar una tarea por hecha.

## Non-negotiable

1. Nunca `git push`, ni crear el fork, ni abrir el pull request: son pasos manuales de la persona.
2. Nunca implementar un non-goal: login, Docker, base persistente, tema oscuro, i18n, WebSocket,
   Lombok, tarifas editables, multi-estacionamiento, Signal Forms, E2E.
3. Nunca editar una migración Flyway ya confirmada; un cambio de esquema es un archivo `V<n+1>` nuevo.
4. Nunca importar Spring o JPA en `domain` ni en `application`.
5. Nunca bajar un umbral ni editar un comando de verificación para que una puerta pase.
6. Nunca marcar una tarea como hecha con una puerta en rojo.
7. Nunca colocar archivos del blueprint dentro de `backend/` o `frontend/`.

El contrato de negocio es `Requerimientos.md`. Arquitectura completa, límites entre capas y tokens
de diseño: ver `CLAUDE.md` en este directorio. Orden de construcción:
`blueprints/gestion-acceso-vehicular/tasks.json`.
