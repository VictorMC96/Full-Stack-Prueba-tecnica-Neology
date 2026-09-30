# Gestión de Acceso Vehicular

Sistema web para gestionar el acceso de vehículos a un estacionamiento (entradas, salidas, cobros,
reporte mensual). Prueba técnica Full Stack para Neology: la usan operadores de caseta y la evalúan
técnicos que clonan el repositorio y siguen el README.

## Commands

Shell: Git Bash. Cada comando se ejecuta desde la raíz del proyecto.

| Task | Command |
|---|---|
| Backend — arrancar | `(cd backend && ./mvnw spring-boot:run)` — http://localhost:8080 |
| Backend — puerta completa | `(cd backend && ./mvnw -q -B -ntp verify)` — pruebas + Spotless + JaCoCo |
| Backend — pruebas | `(cd backend && ./mvnw -q -B -ntp test)` |
| Backend — una clase de prueba | `(cd backend && ./mvnw -q -B -ntp test -Dtest=PlacaTest)` |
| Backend — formatear | `(cd backend && ./mvnw -q -B -ntp spotless:apply)` |
| Frontend — instalar | `(cd frontend && npm ci)` |
| Frontend — arrancar | `(cd frontend && npm start)` — http://localhost:4200, proxy de `/neo` al backend |
| Frontend — pruebas | `(cd frontend && npm run test:ci)` — Karma en ChromeHeadless |
| Frontend — formato | `(cd frontend && npm run format:check)` · corregir: `npm run format` |
| Frontend — build | `(cd frontend && npm run build)` |
| Swagger UI | http://localhost:8080/swagger-ui.html · JSON en `/v3/api-docs` |

**Gate:** `(cd backend && ./mvnw -q -B -ntp verify)` y `(cd frontend && npm run format:check && npm run test:ci && npm run build)`
deben salir con 0 antes de marcar cualquier tarea como hecha.

Maven no está instalado: usa siempre el wrapper `./mvnw`. No hay Docker. Las versiones viven en
`backend/pom.xml` y `frontend/package.json` (+ `package-lock.json`): léelas, nunca las adivines.
Si Karma no encuentra Chrome: `export CHROME_BIN="/c/Program Files/Google/Chrome/Application/chrome.exe"`.

## Stack

Java 21 · Spring Boot 4 (webmvc, Data JPA, Validation, Actuator) · H2 en memoria · Flyway ·
springdoc-openapi · Apache POI · Angular 22 standalone y zoneless · Angular Material 3 · SCSS ·
Karma + Jasmine · sin autenticación · sin despliegue (ejecución local, CI en GitHub Actions).

## Architecture

**Request path.** Navegador → `frontend/src/app/features/*` → `core/api/*-api.ts` (`HttpClient`,
ruta relativa `/neo/...`) → proxy de `ng serve` → `backend/.../web/*Controller` →
`application/<CasoDeUso>` → puerto en `application/puerto/` → adaptador en
`infrastructure/persistencia/` → H2. Los errores vuelven como `ProblemDetail` desde
`web/ApiExceptionHandler` y el frontend los muestra con `core/interceptores/problem-detail.interceptor.ts`.

**Boundaries.** Paquete base `mx.neology.acceso`.

| Layer | May import from | Must never |
|---|---|---|
| `domain` | `java.*` | Spring, JPA, Jackson, POI |
| `application` | `domain`, `java.*` | Spring, JPA, `Pageable`, entidades |
| `infrastructure` | `application`, `domain`, Spring, JPA, POI | `web` |
| `web` | `application`, `domain`, Spring MVC | Repositorios Spring Data, entidades JPA |
| `frontend/.../features/*` | `core`, `shared` | Otra feature; `HttpClient` directo |
| `frontend/.../core`, `shared` | Angular, Material | `features` |

**Where things live.**

| Concern | Single source of truth |
|---|---|
| Contrato de negocio | `Requerimientos.md` (enunciado + "Decisiones técnicas") |
| Esquema y datos iniciales | `backend/src/main/resources/db/migration/V*.sql` — Hibernate solo valida |
| Tarifas y zona horaria | `backend/src/main/resources/application.yml` → `acceso.*` |
| Cálculo de cobro | `domain/cobro/` — una `EstrategiaCobro` por tipo + `CalculadoraCobro` |
| Normalización de placa | `domain/Placa.java` y `frontend/src/app/shared/placa/placa.validator.ts` (misma regla) |
| Códigos de error | `domain/excepcion/` + `web/ApiExceptionHandler.java` |
| Tipos de la API en el frontend | `frontend/src/app/core/modelos/modelos.ts` |
| Tokens de diseño | `frontend/src/styles.scss` |
| Orden de construcción | `blueprints/gestion-acceso-vehicular/tasks.json` y `epics/` |

## Code rules

1. **Sin `if`/`switch` sobre `TipoVehiculo`** en casos de uso ni controladores: el cobro y el momento de
   cobro se piden a `CalculadoraCobro`.
2. **Sin Lombok.** `record` para DTOs y value objects.
3. **Dinero:** `BigDecimal` con `setScale(2, RoundingMode.HALF_UP)`; columnas `DECIMAL(12,2)`.
   Minutos: todo minuto iniciado cuenta completo (`Estancia.minutosCompletos`, redondeo hacia arriba).
4. **Tiempo:** toda fecha sale de `OffsetDateTime.now(clock)` con el `Clock` inyectado. Prohibido
   `now()` sin reloj y prohibido aceptar fechas del cliente.
5. **Placa:** siempre normalizada (mayúsculas, sin espacios ni guiones) y validada con
   `^[A-Z0-9]{3,10}$` antes de tocar la base de datos.
6. **Errores:** solo `ProblemDetail` con `code`; nunca un 200 con error dentro ni una traza al cliente.
7. **Listas:** paginación, orden y filtro en la consulta SQL; tamaño máximo 100.
8. **Pruebas backend:** `@MockitoBean` (nunca `@MockBean`); JSON con `jsonPath`; las pruebas que
   escriben llevan `@Transactional` para no contaminar la precarga.
9. **Frontend:** componentes standalone, signals, `@if`/`@for`, Reactive Forms; cada página con un
   solo `h1` y con estados de carga, vacío y error.
10. **Pruebas frontend:** `await fixture.whenStable()`; sin `fakeAsync` (la app es zoneless).
11. **Sin `innerHTML`** y sin colores hexadecimales en archivos de componentes.
12. **Commits:** Conventional Commits + `Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>`.

## Design system

Material 3, solo tema claro, densidad 0. Tokens en `frontend/src/styles.scss`.

| Role | Value | Used for |
|---|---|---|
| Primary | `#0F4C5C` | Barra superior, botones principales, enlaces, foco |
| Accent (tertiary) | `#E36414` | Estado DENTRO, acción secundaria; nunca texto de cuerpo sobre blanco |
| Background | `#F8FAFB` | Fondo de página |
| Surface | `#FFFFFF` | Tarjetas, tabla, diálogos |
| Border | `#74777B` | Contorno de campos |
| Text | `#1A1C1E` | Texto de cuerpo |
| Muted text | `#5C6670` | Texto secundario |
| Destructive | `#B3261E` | Errores |
| Success | `#2E7D32` | Confirmaciones |

- **Type:** Inter 400/500/600 (h1 1.75rem/600, h2 1.25rem/600, cuerpo 1rem/400); placas en Roboto Mono
  500 con `letter-spacing: 0.08em` (clase `.placa`).
- **Spacing:** base 4px — 4, 8, 12, 16, 24, 32, 48 (`--app-space-1` … `--app-space-7`).
- **Radius:** 8px tarjetas, 4px chips. **Elevation:** tarjetas `outlined`, sin sombras propias.
- **Motion:** solo las transiciones de Material; se respeta `prefers-reduced-motion`.
- **Layout:** ancho máximo 1200px; cortes en 600px y 960px. **Icons:** Material Symbols Outlined.
- **Chips:** OFICIAL `primary-container` · RESIDENTE `secondary-container` · NO_RESIDENTE
  `surface-variant` · DENTRO `tertiary-container` · FUERA contorno `outline`; siempre con texto.

## Environment

No hay variables de entorno obligatorias ni archivo `.env`. Configuración en `application.yml`.

| Variable | Required | Used by | Source |
|---|---|---|---|
| `CHROME_BIN` | no | Karma, solo si no detecta Chrome | Ruta de `chrome.exe` instalada en la máquina |

## Flujo de trabajo

- El blueprint (`blueprints/gestion-acceso-vehicular/`) es la fuente de verdad de la arquitectura y
  del orden de construcción. No reabras las decisiones de `Requerimientos.md`.
- Cada tarea puede ejecutarse con `/architect-next` (lee `tasks.json`) o alimentando la épica a
  Spec Kit: `/speckit-specify` → `/speckit-plan` → `/speckit-tasks` → `/speckit-implement`.
- Rama de trabajo: `feature/xochitlgr`. Una rama por épica o feature desde ella, integrada con
  `git merge --no-ff`. Tras cada tarea: commit + etiqueta `step-NN-<slug>`.
- Precedencia cuando dos fuentes discrepan: enunciado + `Requerimientos.md` → este `CLAUDE.md` y la
  constitución de Spec Kit → skills específicas (`springboot-patterns`, `java-coding-standards`,
  `jpa-patterns`, `springboot-tdd`, `angular-developer`) → skills generales (`coding-standards`,
  `api-design`).

## Rules

| File | Applies to |
|---|---|
| `.claude/rules/backend.md` | `backend/**` |
| `.claude/rules/frontend.md` | `frontend/**` |

## Non-negotiable

1. Nunca `git push`, ni crear el fork, ni abrir el pull request: son pasos manuales de la persona.
2. Nunca implementar un non-goal: login, Docker, base persistente, tema oscuro, i18n, WebSocket,
   Lombok, tarifas editables, multi-estacionamiento, Signal Forms, E2E.
3. Nunca editar una migración Flyway ya confirmada; un cambio de esquema es un archivo `V<n+1>` nuevo.
4. Nunca importar Spring o JPA en `domain` ni en `application`.
5. Nunca bajar un umbral ni editar un comando de verificación para que una puerta pase.
6. Nunca marcar una tarea como hecha con una puerta en rojo.
7. Nunca colocar archivos del blueprint dentro de `backend/` o `frontend/`.
