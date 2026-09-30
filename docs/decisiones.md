# Decisiones técnicas

Registro breve, estilo ADR, de las decisiones que no se deducen del código. El contrato de negocio
está en [Requerimientos.md](../Requerimientos.md); la arquitectura completa en
[blueprints/gestion-acceso-vehicular/blueprint.md](../blueprints/gestion-acceso-vehicular/blueprint.md).

| # | Decisión | Motivo |
|---|----------|--------|
| 1 | **Spring Boot 4.1.1** en lugar de la línea "3.x" del enunciado | Al 30-09-2026 Spring Initializr solo ofrece 4.0.8 / 4.1.1 y la línea 3.x ya no tiene soporte OSS. Se cumple el espíritu del requisito (Java 17+, Jakarta, Spring 3.x+) con una versión soportada. |
| 2 | **Angular 22.2** con Angular Material 22.2 | Última estable; cumple "Angular 16+". Standalone, signals, control flow `@if/@for`, zoneless. |
| 3 | **Jasmine + Karma** (`ng new --test-runner=karma`) | Lo pide el enunciado; Vitest es el runner por defecto de Angular 22 pero Karma sigue soportado. |
| 4 | **Formularios reactivos**, no Signal Forms | Integración madura con `mat-form-field` y `mat-error`. |
| 5 | **Clean Architecture pragmática** (`domain` / `application` / `infrastructure` / `web`) | Dominio puro testeable sin Spring; entidades JPA separadas del modelo de dominio. |
| 6 | **Strategy** para el cobro por tipo de vehículo, tarifas en `application.yml` | Requisito "agregar fácilmente nuevos tipos": una clase + una constante + una entrada de configuración. |
| 7 | **`BigDecimal`** escala 2 `HALF_UP`; **minutos completos**, segundos truncados | Decisión de negocio acordada; `Duration.toMinutes()`. |
| 8 | Fecha/hora la asigna el **servidor** (`Clock` inyectado, `America/Mexico_City`) | Evita manipulación desde el cliente y hace testeable el tiempo. |
| 9 | Placa normalizada (mayúsculas, sin espacios ni guiones) y validada con `^[A-Z0-9]{3,10}$` en front y back | Cubre formatos mexicanos y extranjeros; cierra la puerta a XSS por caracteres especiales. |
| 10 | Vehículo **no registrado** que entra → se da de alta como **no residente**; puede **reclasificarse** después a oficial/residente | El alta de no residentes es opcional en el enunciado; los visitantes no deben rechazarse. |
| 11 | **Reinicio de mes archiva**, no borra: se cierra el periodo activo y se abre otro; acumulados por periodo | Trazabilidad de lo cobrado. Estancias abiertas cuentan en el periodo en que salen. |
| 12 | `GET neo/residentes/pagos` con **negociación de contenido** (`Accept`): JSON o `.xlsx` (Apache POI) | Una sola ruta, como fija el enunciado; forma REST canónica. |
| 13 | Errores **RFC 7807** (`ProblemDetail` + `code` + `errors[]`) | Estándar soportado por Spring; el front los muestra con `MatSnackBar`. |
| 14 | **Flyway** también sobre H2 (`V1__esquema.sql`, `V2__datos_iniciales.sql`), `ddl-auto=validate` | Esquema versionado y base precargada reproducible. |
| 15 | Una estancia abierta por vehículo garantizada con `vehiculos.estancia_abierta_id UNIQUE` + bloqueo pesimista | H2 no soporta índices parciales. |
| 16 | **Sin login**, sin Docker, sin BD persistente en v1 | No lo pide el enunciado; 5 h de presupuesto. Documentado como mejora futura. |
| 17 | Sin **Lombok**: `record` para DTOs y value objects | Inmutabilidad por defecto; menos magia. |
| 18 | Rama de entrega `feature/xochitlgr` a partir de `upstream/main`, PR al repositorio de Neology | Convención observada en los PRs existentes del repositorio. |
| 19 | Operación de **un clic desde el listado**: el chip «Fuera» registra la entrada y «Dentro» la salida; el alta registra la entrada por defecto (interruptor «Registrar entrada ahora») | Acerca la acción al dato y ahorra clics al operador. La hora la sigue asignando el servidor. |
| 20 | En H2 el `CHECK` de tipo de vehículo usa una cadena de `OR` en vez de `IN (...)` | H2 evalúa `IN` con la sesión que creó la tabla; cuando HikariCP retira esa conexión (`maxLifetime`, 30 min) las escrituras fallan con "The database has been closed". Detectado en pruebas manuales. |
