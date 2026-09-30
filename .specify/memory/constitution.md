# Gestión de Acceso Vehicular — Constitución

Fuente de verdad de arquitectura y orden de construcción: `blueprints/gestion-acceso-vehicular/`
(blueprint.md, épicas y tasks.json). Esta constitución fija los principios que ninguna feature puede
contradecir. El contrato de negocio es `Requerimientos.md`.

## Principios

### I. El enunciado es contrato
Las rutas `neo/...`, los modelos y las reglas de cobro de `Requerimientos.md` no se cambian. Toda
desviación (p. ej. Spring Boot 4.1.1 en lugar de "3.x") se documenta en `docs/decisiones.md`.

### II. Clean Architecture pragmática
Backend en `mx.neology.acceso.{domain,application,infrastructure,web}`. `domain` y `application` no
importan Spring ni JPA. Los tipos de vehículo son un catálogo administrable (tabla `tipos_vehiculo`) y
el cobro usa Strategy por modelo (`EstrategiaCobro`: sin cobro, por minuto al salir, por minuto
acumulado); agregar un tipo = una fila desde la interfaz; agregar un modelo = una clase + una constante.

### III. Tests primero donde vive la lógica (NO NEGOCIABLE)
Dominio y casos de uso se desarrollan con TDD (JUnit 5 + Mockito + AssertJ). Controladores con
`@WebMvcTest`/`@MockitoBean`, persistencia con `@DataJpaTest` sobre H2 + Flyway. JaCoCo ≥ 80 % en
`domain` + `application`. Frontend: Jasmine + Karma en cada componente y servicio.

### IV. Dinero y tiempo exactos
`BigDecimal` con 2 decimales (`HALF_UP`); todo minuto iniciado se cobra completo; toda fecha/hora la
asigna el servidor con un `Clock` inyectado (`America/Mexico_City`).

### V. Validar en ambos extremos, errores estándar
Placa normalizada (mayúsculas, sin espacios ni guiones) y validada con `^[A-Z0-9]{3,10}$` en Angular y
en Bean Validation. Errores en RFC 7807 (`ProblemDetail` + `code` + `errors`). 201 + `Location` al
crear, 409 en conflictos de estado, 400 en validación. Sin `innerHTML`.

### VI. Interfaz cuidada y accesible
Angular Material M3 con tema propio (primario `#0F4C5C`, acento `#E36414`), formularios reactivos,
estados de carga/vacío/error en cada lista, un `h1` por página, etiquetas reales en cada campo,
confirmación antes de acciones irreversibles (iniciar mes).

### VII. Simplicidad (YAGNI)
Sin login, Docker, caché, colas ni Lombok en v1. Lo que no pide el enunciado se documenta como mejora
futura en el README, no se implementa a medias.

## Stack fijado
Java 21 · Spring Boot 4.1.1 · JPA/Hibernate · H2 en memoria · Flyway · springdoc 3.1.1 · Apache POI
5.5.1 · Maven Wrapper · Angular 22.2 · Angular Material 22.2 · SCSS · Jasmine + Karma · GitHub Actions.

## Flujo de desarrollo
- Rama de entrega `feature/xochitlgr` (desde `upstream/main`). Por límite de tiempo, las épicas se
  implementan directamente sobre ella; una rama por feature solo si el trabajo supera una sesión.
- Conventional Commits (`type(scope): resumen`), trailer `Co-Authored-By`.
- Cada paso del blueprint termina con su bloque `Verify` en verde antes de pasar al siguiente.
- Nunca `git push` ni creación de fork/PR sin confirmación explícita de la persona.

## Precedencia
`Requerimientos.md` → esta constitución → `CLAUDE.md`/blueprint → skills específicas
(`springboot-patterns`, `jpa-patterns`, `springboot-tdd`, `api-design`, `angular-developer`) →
skills generales (`java-coding-standards`, `coding-standards`).

**Versión**: 1.0.0 | **Ratificada**: 2026-09-30 | **Última modificación**: 2026-09-30
