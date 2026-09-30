# Epic 02: Backend

> Al terminar esta épica el backend Spring Boot expone los 10 endpoints bajo `/neo` con H2 precargada,
> errores `ProblemDetail`, documentación OpenAPI, formato verificado y cobertura ≥ 80 % en dominio y
> aplicación.

| | |
|---|---|
| **Epic id** | `02-backend` |
| **Tasks** | `E2-T1` … `E2-T7` |
| **Depends on** | nothing — start here (solo requiere el bloque Bootstrap de `blueprint.md` §10) |
| **Unlocks** | `04-calidad-entrega` |
| **Parallel with** | `01-base`, `03-frontend` — no comparten archivos |

You do not need any other file to complete this epic. Everything below is repeated here on purpose.

---

## Stack

Java 21 · Spring Boot 4 (`spring-boot-starter-webmvc`, Data JPA, Validation, Actuator) · Hibernate ·
H2 en memoria · Flyway · springdoc-openapi · Apache POI · JUnit Jupiter + Mockito + AssertJ · Spotless
(google-java-format) · JaCoCo. Shell: **Git Bash**, desde la raíz del proyecto. Maven no está
instalado: siempre `./mvnw`. `backend/pom.xml` lo generó Spring Initializr: solo se le añaden los
fragmentos que indican las tareas. Las versiones de los fragmentos son las de la tabla de
dependencias del blueprint (2026-09-30); las gestionadas por el BOM de Spring Boot no llevan versión.

| Task | Command |
|---|---|
| Arrancar | `(cd backend && ./mvnw spring-boot:run)` — http://localhost:8080 · Swagger en `/swagger-ui.html` |
| Puerta completa | `(cd backend && ./mvnw -q -B -ntp verify)` |
| Pruebas | `(cd backend && ./mvnw -q -B -ntp test)` |
| Una clase de prueba | `(cd backend && ./mvnw -q -B -ntp test -Dtest=PlacaTest)` |
| Formatear | `(cd backend && ./mvnw -q -B -ntp spotless:apply)` |
| Pureza de capas | `grep -rlE "org\.springframework\|jakarta\.persistence" backend/src/main/java/mx/neology/acceso/domain backend/src/main/java/mx/neology/acceso/application; test $? -eq 1` |

**Gate:** `(cd backend && ./mvnw -q -B -ntp verify)` passes before any task here is marked done. Cada
línea de `Verify` se ejecuta en su propio shell desde la raíz del proyecto.

No hay servicios locales: H2 corre embebida. Nada que arrancar ni parar.

## Directory subtree

Only the parts this epic touches:

```
backend/
  pom.xml                                   # exists (Initializr) — E2-T1 y E2-T7 le añaden fragmentos
  src/main/resources/
    application.yml                         # NEW en E2-T1 (sustituye a application.properties, que se borra)
    db/migration/V1__esquema.sql            # NEW en E2-T1 — SQL literal de abajo
    db/migration/V2__datos_iniciales.sql    # NEW en E2-T1 — SQL literal de abajo
  src/main/java/mx/neology/acceso/
    BackendApplication.java                 # exists — E2-T1 añade @ConfigurationPropertiesScan
    domain/                                 # E2-T2 — Java puro: Placa, TipoVehiculo, MomentoCobro,
      cobro/                                #   Vehiculo, Estancia, Periodo, Acumulado, cobro/, excepcion/
      excepcion/
    application/                            # E2-T3..E2-T7 — casos de uso y puertos, sin Spring
      puerto/
      modelo/
    infrastructure/
      config/                               # E2-T1: TarifasProperties, ClockConfig · E2-T3+: CasosDeUsoConfig
      persistencia/                         # E2-T3+: entidades JPA, repos Spring Data, adaptadores, mappers
      excel/                                # E2-T6: InformeExcelWriter
    web/                                    # E2-T3+: controladores, records, ApiExceptionHandler, PageResponse
  src/test/java/mx/neology/acceso/          # espejo de main; soporte/RelojDePrueba en E2-T4
```

Everything outside this subtree is out of scope. If a task seems to require editing a file not
listed here, stop and report — it means the epic boundary is wrong.

## Data model touched here

| Entity | Fields this epic adds or reads | Notes |
|---|---|---|
| `periodos` | `id`, `inicio`, `fin`, `activo`, `created_at`, `updated_at` | `activo` es `TRUE` o `NULL` (nunca `FALSE`); `UNIQUE (activo)` garantiza un solo periodo activo |
| `vehiculos` | `id`, `placa`, `tipo`, `estancia_abierta_id`, `created_at`, `updated_at` | `placa` única y normalizada; `estancia_abierta_id` único y anulable = una sola estancia abierta |
| `estancias` | `id`, `vehiculo_id`, `entrada`, `salida`, `minutos`, `importe`, `periodo_id`, `created_at`, `updated_at` | `periodo_id` se fija al cerrar; abiertas tienen `salida`, `minutos`, `importe`, `periodo_id` en `NULL` |
| `acumulados_residentes` | `id`, `vehiculo_id`, `periodo_id`, `minutos`, `importe`, `created_at`, `updated_at` | Única por `(vehiculo_id, periodo_id)`; el periodo nuevo empieza sin filas |

Mapeo JPA que `ddl-auto=validate` debe aceptar: `UUID` → `java.util.UUID`; `TIMESTAMP WITH TIME ZONE`
→ `OffsetDateTime`; `DECIMAL(12,2)` → `BigDecimal` con `@Column(precision = 12, scale = 2)`; `tipo` →
`@Enumerated(EnumType.STRING)` + `@Column(length = 20)`; `activo` → `Boolean`; `minutos` → `Integer`.

`backend/src/main/resources/db/migration/V1__esquema.sql` (copiar literal):

```sql
CREATE TABLE periodos (
    id         UUID PRIMARY KEY,
    inicio     TIMESTAMP WITH TIME ZONE NOT NULL,
    fin        TIMESTAMP WITH TIME ZONE,
    activo     BOOLEAN,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT ck_periodos_activo CHECK (activo IS NULL OR activo = TRUE),
    CONSTRAINT uq_periodos_activo UNIQUE (activo)
);

CREATE TABLE vehiculos (
    id                  UUID PRIMARY KEY,
    placa               VARCHAR(10) NOT NULL,
    tipo                VARCHAR(20) NOT NULL,
    estancia_abierta_id UUID,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_vehiculos_placa UNIQUE (placa),
    CONSTRAINT uq_vehiculos_estancia_abierta UNIQUE (estancia_abierta_id),
    CONSTRAINT ck_vehiculos_tipo CHECK (tipo IN ('OFICIAL', 'RESIDENTE', 'NO_RESIDENTE'))
);

CREATE TABLE estancias (
    id          UUID PRIMARY KEY,
    vehiculo_id UUID NOT NULL,
    entrada     TIMESTAMP WITH TIME ZONE NOT NULL,
    salida      TIMESTAMP WITH TIME ZONE,
    minutos     INT,
    importe     DECIMAL(12, 2),
    periodo_id  UUID,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_estancias_vehiculo FOREIGN KEY (vehiculo_id) REFERENCES vehiculos (id),
    CONSTRAINT fk_estancias_periodo FOREIGN KEY (periodo_id) REFERENCES periodos (id)
);

CREATE INDEX idx_estancias_vehiculo_id ON estancias (vehiculo_id);
CREATE INDEX idx_estancias_periodo_id ON estancias (periodo_id);
CREATE INDEX idx_estancias_vehiculo_entrada ON estancias (vehiculo_id, entrada DESC);

ALTER TABLE vehiculos
    ADD CONSTRAINT fk_vehiculos_estancia_abierta
        FOREIGN KEY (estancia_abierta_id) REFERENCES estancias (id);

CREATE TABLE acumulados_residentes (
    id          UUID PRIMARY KEY,
    vehiculo_id UUID NOT NULL,
    periodo_id  UUID NOT NULL,
    minutos     INT NOT NULL DEFAULT 0,
    importe     DECIMAL(12, 2) NOT NULL DEFAULT 0,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_acumulados_vehiculo FOREIGN KEY (vehiculo_id) REFERENCES vehiculos (id),
    CONSTRAINT fk_acumulados_periodo FOREIGN KEY (periodo_id) REFERENCES periodos (id),
    CONSTRAINT uq_acumulados_vehiculo_periodo UNIQUE (vehiculo_id, periodo_id)
);

CREATE INDEX idx_acumulados_periodo_id ON acumulados_residentes (periodo_id);
```

`backend/src/main/resources/db/migration/V2__datos_iniciales.sql` (copiar literal):

```sql
INSERT INTO periodos (id, inicio, fin, activo, created_at, updated_at) VALUES
    ('00000000-0000-0000-0000-000000000001', TIMESTAMP WITH TIME ZONE '2026-09-01 00:00:00-06:00', NULL, TRUE,
     TIMESTAMP WITH TIME ZONE '2026-09-01 00:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-01 00:00:00-06:00');

INSERT INTO vehiculos (id, placa, tipo, estancia_abierta_id, created_at, updated_at) VALUES
    ('00000000-0000-0000-0000-000000000011', 'OFI001', 'OFICIAL', NULL,
     TIMESTAMP WITH TIME ZONE '2026-09-01 08:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-01 08:00:00-06:00'),
    ('00000000-0000-0000-0000-000000000012', 'OFI002', 'OFICIAL', NULL,
     TIMESTAMP WITH TIME ZONE '2026-09-01 08:05:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-01 08:05:00-06:00'),
    ('00000000-0000-0000-0000-000000000021', 'RES001', 'RESIDENTE', NULL,
     TIMESTAMP WITH TIME ZONE '2026-09-01 08:10:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-01 08:10:00-06:00'),
    ('00000000-0000-0000-0000-000000000022', 'RES002', 'RESIDENTE', NULL,
     TIMESTAMP WITH TIME ZONE '2026-09-01 08:15:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-01 08:15:00-06:00'),
    ('00000000-0000-0000-0000-000000000023', 'RES003', 'RESIDENTE', NULL,
     TIMESTAMP WITH TIME ZONE '2026-09-01 08:20:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-01 08:20:00-06:00'),
    ('00000000-0000-0000-0000-000000000031', 'NOR001', 'NO_RESIDENTE', NULL,
     TIMESTAMP WITH TIME ZONE '2026-09-06 12:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-06 12:00:00-06:00');

INSERT INTO estancias (id, vehiculo_id, entrada, salida, minutos, importe, periodo_id, created_at, updated_at) VALUES
    ('00000000-0000-0000-0000-000000000101', '00000000-0000-0000-0000-000000000021',
     TIMESTAMP WITH TIME ZONE '2026-09-02 08:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-02 09:30:00-06:00',
     90, 4.50, '00000000-0000-0000-0000-000000000001',
     TIMESTAMP WITH TIME ZONE '2026-09-02 08:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-02 09:30:00-06:00'),
    ('00000000-0000-0000-0000-000000000102', '00000000-0000-0000-0000-000000000021',
     TIMESTAMP WITH TIME ZONE '2026-09-03 08:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-03 10:00:00-06:00',
     120, 6.00, '00000000-0000-0000-0000-000000000001',
     TIMESTAMP WITH TIME ZONE '2026-09-03 08:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-03 10:00:00-06:00'),
    ('00000000-0000-0000-0000-000000000103', '00000000-0000-0000-0000-000000000022',
     TIMESTAMP WITH TIME ZONE '2026-09-02 07:15:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-02 07:45:00-06:00',
     30, 1.50, '00000000-0000-0000-0000-000000000001',
     TIMESTAMP WITH TIME ZONE '2026-09-02 07:15:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-02 07:45:00-06:00'),
    ('00000000-0000-0000-0000-000000000104', '00000000-0000-0000-0000-000000000023',
     TIMESTAMP WITH TIME ZONE '2026-09-04 18:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-04 19:00:00-06:00',
     60, 3.00, '00000000-0000-0000-0000-000000000001',
     TIMESTAMP WITH TIME ZONE '2026-09-04 18:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-04 19:00:00-06:00'),
    ('00000000-0000-0000-0000-000000000105', '00000000-0000-0000-0000-000000000011',
     TIMESTAMP WITH TIME ZONE '2026-09-05 09:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-05 17:00:00-06:00',
     480, 0.00, '00000000-0000-0000-0000-000000000001',
     TIMESTAMP WITH TIME ZONE '2026-09-05 09:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-05 17:00:00-06:00'),
    ('00000000-0000-0000-0000-000000000106', '00000000-0000-0000-0000-000000000031',
     TIMESTAMP WITH TIME ZONE '2026-09-06 12:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-06 12:45:00-06:00',
     45, 22.50, '00000000-0000-0000-0000-000000000001',
     TIMESTAMP WITH TIME ZONE '2026-09-06 12:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-06 12:45:00-06:00'),
    ('00000000-0000-0000-0000-000000000107', '00000000-0000-0000-0000-000000000012',
     TIMESTAMP WITH TIME ZONE '2026-09-07 08:00:00-06:00', NULL,
     NULL, NULL, NULL,
     TIMESTAMP WITH TIME ZONE '2026-09-07 08:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-07 08:00:00-06:00');

UPDATE vehiculos
   SET estancia_abierta_id = '00000000-0000-0000-0000-000000000107'
 WHERE id = '00000000-0000-0000-0000-000000000012';

INSERT INTO acumulados_residentes (id, vehiculo_id, periodo_id, minutos, importe, created_at, updated_at) VALUES
    ('00000000-0000-0000-0000-000000000201', '00000000-0000-0000-0000-000000000021',
     '00000000-0000-0000-0000-000000000001', 210, 10.50,
     TIMESTAMP WITH TIME ZONE '2026-09-03 10:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-03 10:00:00-06:00'),
    ('00000000-0000-0000-0000-000000000202', '00000000-0000-0000-0000-000000000022',
     '00000000-0000-0000-0000-000000000001', 30, 1.50,
     TIMESTAMP WITH TIME ZONE '2026-09-02 07:45:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-02 07:45:00-06:00'),
    ('00000000-0000-0000-0000-000000000203', '00000000-0000-0000-0000-000000000023',
     '00000000-0000-0000-0000-000000000001', 60, 3.00,
     TIMESTAMP WITH TIME ZONE '2026-09-04 19:00:00-06:00', TIMESTAMP WITH TIME ZONE '2026-09-04 19:00:00-06:00');
```

Números exactos de la precarga, contados de los `INSERT`: 1 periodo activo (inicio
`2026-09-01T00:00:00-06:00`) · 6 vehículos (`OFI001`, `OFI002` oficiales; `RES001`, `RES002`, `RES003`
residentes; `NOR001` no residente) · 7 estancias (6 cerradas en el periodo, 1 abierta de `OFI002`) ·
acumulados `RES001` 210 min / 10.50, `RES002` 30 / 1.50, `RES003` 60 / 3.00 · total del informe 300 min /
15.00 · `NOR001` pagó 45 min / 22.50. Placas ordenadas: `NOR001`, `OFI001`, `OFI002`, `RES001`,
`RES002`, `RES003`.

## Contracts

**Consumed** — already exists, do not rebuild:

| From | Interface | Guarantee |
|---|---|---|
| Bootstrap | `backend/` generado por Spring Initializr con `mx.neology.acceso.BackendApplication`, `./mvnw`, starters web (`webmvc`), data-jpa, h2, flyway, validation, actuator y sus starters de prueba | `./mvnw test` pasó en Bootstrap |
| `Requerimientos.md` | Reglas de negocio y decisiones técnicas | Contrato; no se reabre |

**Produced** — later epics depend on exactly these signatures. Changing one breaks them:

| Export | Signature | Used by |
|---|---|---|
| `POST /neo/vehiculos/oficiales` · `/residentes` · `/no-residentes` | body `{ "placa": string }` → 201 + `Location: /neo/vehiculos/{placa}` + `VehiculoResponse`; 400 `VALIDACION`; 409 `VEHICULO_YA_REGISTRADO` | `03-frontend` (alta) |
| `POST /neo/estancias/entrada` | `{ placa }` → 201 `EstanciaResponse`; 409 `VEHICULO_YA_DENTRO`; placa desconocida se crea como `NO_RESIDENTE` | `03-frontend` |
| `POST /neo/estancias/salida` | `{ placa }` → 200 `EstanciaResponse` con `minutos`, `importe`, `momentoCobro`; 409 `VEHICULO_NO_DENTRO`; 404 `VEHICULO_NO_ENCONTRADO` | `03-frontend` |
| `GET /neo/residentes/pagos` | `Accept: application/json` → `InformePagosResponse`; `Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` → `.xlsx` con `Content-Disposition: attachment; filename="informe-pagos-<yyyy-MM>.xlsx"` | `03-frontend` |
| `POST /neo/mes/iniciar` | sin cuerpo → 200 `IniciarMesResponse` | `03-frontend` |
| `GET /neo/vehiculos` | `?placa=&page=0&size=10&sort=placa,asc` → `PageResponse<VehiculoResumen>`; `size` > 100 → 100; `sort` fuera de `placa`, `tipo`, `createdAt` → 400 `PARAMETRO_INVALIDO` | `03-frontend` |
| `GET /neo/vehiculos/{placa}` | → `VehiculoDetalleResponse`; 404 `VEHICULO_NO_ENCONTRADO` | `03-frontend` |
| `GET /neo/vehiculos/{placa}/estancias` | `?page&size` → `PageResponse<EstanciaResponse>` por `entrada` desc | `03-frontend` |
| Errores | `application/problem+json` con `type`, `title`, `status`, `detail`, `instance`, `code` y, en validación, `errors: [{ field, message }]`. Códigos: `VALIDACION` 400, `PARAMETRO_INVALIDO` 400, `VEHICULO_NO_ENCONTRADO` 404, `VEHICULO_YA_REGISTRADO` 409, `VEHICULO_YA_DENTRO` 409, `VEHICULO_NO_DENTRO` 409, `ERROR_INTERNO` 500 | `03-frontend` (interceptor) |

Formas (JSON):

```
VehiculoResponse        { id, placa, tipo, dentro, createdAt }
VehiculoResumen         { placa, tipo, dentro, createdAt }
EstanciaResponse        { id, placa, tipo, entrada, salida|null, minutos|null, importe|null, momentoCobro, periodoId|null }
AcumuladoResponse       { minutos, importe }
VehiculoDetalleResponse { id, placa, tipo, dentro, estanciaAbierta: EstanciaResponse|null, acumuladoPeriodo: AcumuladoResponse|null, createdAt }
PeriodoResponse         { id, inicio, fin|null }
InformePagosResponse    { periodo: PeriodoResponse, filas: [{ placa, minutos, importe }], totalMinutos, totalImporte }
IniciarMesResponse      { periodoAnterior: PeriodoResponse, periodoActual: PeriodoResponse }
PageResponse<T>         { content: T[], page, size, totalElements, totalPages }
TipoVehiculo = "OFICIAL" | "RESIDENTE" | "NO_RESIDENTE" · MomentoCobro = "NINGUNO" | "INFORME_MENSUAL" | "AL_SALIR"
```

Fechas ISO-8601 con desplazamiento en zona `America/Mexico_City`; importes con 2 decimales.

## Conventions that bite in this area

- `domain` y `application` no importan Spring, JPA ni POI: el `grep` de cada tarea lo comprueba.
- Frontera transaccional = puerto `Transaccion` (`TransactionTemplate`); nada de `@Transactional` en
  casos de uso.
- Ningún `if`/`switch` sobre `TipoVehiculo` fuera de `CalculadoraCobro` y las estrategias.
- Tiempo: `OffsetDateTime.now(clock)`; nunca `now()` sin reloj. Minutos: `Duration.toMinutes()`.
  Importes: `setScale(2, RoundingMode.HALF_UP)`.
- Spring Boot 4: `@MockitoBean` (nunca `@MockBean`); JSON con `jsonPath`, sin `ObjectMapper`;
  las anotaciones de prueba por capas cambiaron de paquete — deja que el compilador resuelva el import.
- Pruebas de integración que escriben: `@Transactional` para revertir y no contaminar la precarga.
  `@DataJpaTest` con `@AutoConfigureTestDatabase(replace = Replace.NONE)`.
- Antes de cada `verify`: `spotless:apply`. Nunca bajes el umbral de JaCoCo ni saltes pruebas.
- Nunca edites una migración ya confirmada.

Full project rules: `CLAUDE.md`. Area rules: `.claude/rules/backend.md`. Both sit in the project
root — the builder copied them there from the bundle's `workspace/` before task one.

---

## Tasks

Listed in the same order as `tasks.json`. That order is the build order — work top to bottom and do
not re-rank by priority or by what looks quick.

### `E2-T1` — Configurar base del backend, esquema y datos

**Depends on:** nada — solo el Bootstrap · **Priority:** p0 — metadato para recortes de alcance, no un orden de ejecución · **Paso global:** 2

**Objetivo:** El backend arranca con H2 migrada por Flyway, formato verificado por Spotless, y responde en `/actuator/health` y `/v3/api-docs` sobre un puerto real.

Deja el backend arrancable y verificable antes de escribir una sola regla de negocio: dependencias y
formato en el `pom.xml`, configuración, esquema y datos con Flyway, esqueleto de paquetes y dos
pruebas que **arrancan el servidor en un puerto real y le hacen peticiones**. `backend/pom.xml` lo
generó Spring Initializr; aquí solo se le añaden tres bloques, no se reescribe.

**1. `backend/pom.xml`** — añade, sin tocar lo que generó Initializr:

```xml
<!-- dentro de <dependencies> -->
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>3.1.1</version>
</dependency>
<dependency>
  <groupId>org.apache.poi</groupId>
  <artifactId>poi-ooxml</artifactId>
  <version>5.5.1</version>
</dependency>

<!-- dentro de <build><plugins> -->
<plugin>
  <groupId>com.diffplug.spotless</groupId>
  <artifactId>spotless-maven-plugin</artifactId>
  <version>3.10.3</version>
  <configuration>
    <java>
      <includes>
        <include>src/main/java/**/*.java</include>
        <include>src/test/java/**/*.java</include>
      </includes>
      <googleJavaFormat/>
      <removeUnusedImports/>
    </java>
  </configuration>
  <executions>
    <execution>
      <id>spotless-check</id>
      <phase>verify</phase>
      <goals>
        <goal>check</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

Flyway, H2, Hibernate, JUnit, Mockito y AssertJ no llevan versión: las gestiona el BOM de Spring Boot.
Confirma en el `pom.xml` generado que el starter web es `spring-boot-starter-webmvc`; si Initializr
hubiera emitido otro artefacto web, no lo cambies: la prueba de arranque decide.

**2. `backend/src/main/resources/application.yml`** — crea este archivo y **elimina**
`application.properties`:

```yaml
spring:
  application:
    name: gestion-acceso-vehicular
  datasource:
    url: jdbc:h2:mem:acceso;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password: ""
  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
    locations: classpath:db/migration
  mvc:
    problemdetails:
      enabled: true

server:
  port: 8080

acceso:
  zona-horaria: America/Mexico_City
  tarifas:
    residente: 0.05
    no-residente: 0.50

management:
  endpoints:
    web:
      exposure:
        include: health,info

springdoc:
  swagger-ui:
    path: /swagger-ui.html
```

**3. Migraciones** — crea `backend/src/main/resources/db/migration/V1__esquema.sql` y
`V2__datos_iniciales.sql` con el SQL literal del modelo de datos (blueprint §4; repetido íntegro en
"Data model touched here" de la épica 02). Cópialo sin cambios: las pruebas afirman sus números.

**4. Esqueleto de paquetes** bajo `backend/src/main/java/mx/neology/acceso/`:

- `domain/package-info.java`, `application/package-info.java`, `infrastructure/package-info.java`,
  `web/package-info.java` — cada uno con un Javadoc de una línea que diga qué puede importar esa capa.
- `infrastructure/config/TarifasProperties.java` — `@ConfigurationProperties(prefix = "acceso.tarifas")`
  + `@Validated`, como `record TarifasProperties(@NotNull @DecimalMin("0.00") BigDecimal residente,
  @NotNull @DecimalMin("0.00") BigDecimal noResidente)`.
- `infrastructure/config/ClockConfig.java` — `@Configuration` con un `@Bean Clock` que devuelve
  `Clock.system(ZoneId.of(zona))`, leyendo `zona` de `acceso.zona-horaria` con `@Value`.
- `BackendApplication.java` (ya existe) — añade `@ConfigurationPropertiesScan`.

**5. Pruebas** bajo `backend/src/test/java/mx/neology/acceso/`:

- `BaseDatosMigradaTest` — `@SpringBootTest`; con `JdbcTemplate` cuenta las filas de las cuatro
  tablas, suma `importe` de `acumulados_residentes` y comprueba los valores de `TarifasProperties`.
- `ArranqueHttpTest` — `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)`;
  obtiene el puerto con `@Value("${local.server.port}") int puerto` y usa `java.net.http.HttpClient`
  (JDK) contra `http://localhost:<puerto>`: `/actuator/health` → 200 y el cuerpo contiene `UP`;
  `/v3/api-docs` → 200 y el cuerpo contiene `openapi`. Este es el punto de entrada ejercitado en el
  mismo paso que lo crea.

Usa `HttpClient` del JDK y la propiedad `local.server.port` a propósito: son estables entre versiones
de Spring Boot, mientras que las clases de cliente HTTP de prueba cambiaron de paquete en Boot 4. La
prueba `BackendApplicationTests` que generó Initializr se conserva.

**6. Formato** — ejecuta `(cd backend && ./mvnw -q -B -ntp spotless:apply)` antes de verificar: el
código generado por Initializr no viene en estilo google-java-format y `spotless:check` lo rechazaría.

**Files**

- `backend/pom.xml` — edit: añade springdoc, poi-ooxml y el plugin Spotless ligado a `verify`
- `backend/src/main/resources/application.yml` — new: sustituye a `application.properties`, que se elimina
- `backend/src/main/resources/db/migration/*.sql` — new: `V1__esquema.sql` y `V2__datos_iniciales.sql`
- `backend/src/main/java/mx/neology/acceso/**` — new: `package-info.java` por capa, `TarifasProperties`, `ClockConfig`; edit: `BackendApplication`
- `backend/src/test/java/mx/neology/acceso/**` — new: `BaseDatosMigradaTest`, `ArranqueHttpTest`

**Acceptance**

Copiados literalmente del arreglo `acceptance` de esta tarea en `tasks.json`. Cada uno lo decide un comando de abajo, en esta máquina, durante la construcción.

1. **WHEN** the application context starts **THE SYSTEM SHALL** apply the Flyway migrations `V1__esquema.sql` and `V2__datos_iniciales.sql` and start with `spring.jpa.hibernate.ddl-auto` set to `validate`.
2. **WHEN** `BaseDatosMigradaTest` counts rows after startup **THE SYSTEM SHALL** report 6 rows in `vehiculos`, 7 in `estancias`, 1 in `periodos` and 3 in `acumulados_residentes`, with the `importe` column of `acumulados_residentes` summing 15.00.
3. **WHEN** `GET /actuator/health` is requested against the application started on a random port **THE SYSTEM SHALL** return 200 with `status` equal to `UP`.
4. **WHEN** `GET /v3/api-docs` is requested against the application started on a random port **THE SYSTEM SHALL** return 200 with a JSON body containing the key `openapi`.
5. **WHEN** the configuration properties are bound **THE SYSTEM SHALL** expose `acceso.tarifas.residente` as 0.05 and `acceso.tarifas.no-residente` as 0.50 through `TarifasProperties`.
6. **WHEN** `./mvnw -q -B -ntp verify` runs in `backend/` **THE SYSTEM SHALL** exit 0 with `spotless:check` bound to the `verify` phase and reporting no unformatted file.

**Verify** — todos los comandos, en orden, cada uno en su propio shell desde la raíz del proyecto. Cada uno sale con 0 cuando la tarea está bien; la tarea está hecha cuando el último sale con 0.

```bash
(cd backend && ./mvnw -q -B -ntp verify)   # expect: exit 0 — compila, pasa spotless:check y las pruebas de arranque (salud 200, api-docs 200, precarga contada)
grep -q "spotless-maven-plugin" backend/pom.xml   # expect: exit 0
grep -q "springdoc-openapi-starter-webmvc-ui" backend/pom.xml && grep -q "poi-ooxml" backend/pom.xml   # expect: exit 0
test -f backend/src/main/resources/application.yml && test ! -f backend/src/main/resources/application.properties   # expect: exit 0 — un solo archivo de configuración
test -f backend/src/main/resources/db/migration/V1__esquema.sql && test -f backend/src/main/resources/db/migration/V2__datos_iniciales.sql   # expect: exit 0
```

Son el arreglo `verify` de esta tarea en `tasks.json`, una línea por comando, con la afirmación de código de salida intacta (`...; test $? -eq 1`). No los edites ni los "simplifiques".

**Checkpoint**

```bash
git add -A && git commit -q -m "feat(backend): base del backend, esquema Flyway y datos iniciales [E2-T1]" -m "Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
git tag step-02-backend-base
git ls-files --error-unmatch backend/src/main/resources/application.yml   # expect: exit 0
git ls-files --error-unmatch backend/src/main/resources/db/migration/V1__esquema.sql   # expect: exit 0
# punto de retorno si el paso siguiente sale mal: git reset --hard step-02-backend-base
```

Ejecuta el bloque después de que el último comando de Verify salga con 0 y antes de empezar la tarea siguiente. La etiqueta es el valor `checkpoint` de esta tarea en `tasks.json`; si esta tarea sale mal, vuelve con `git reset --hard step-01-repo-base`.

### `E2-T2` — Modelar dominio y estrategias de cobro

**Depends on:** `E2-T1` · **Priority:** p0 — metadato para recortes de alcance, no un orden de ejecución · **Paso global:** 3

**Objetivo:** Existe el dominio puro (placa, tipos, estancia, periodo, estrategias de cobro y calculadora) con sus pruebas unitarias, sin una sola importación de Spring ni de JPA.

Dominio puro en `mx.neology.acceso.domain`, escrito con pruebas primero (TDD): las reglas de placa,
de minutos y de cobro quedan fijadas aquí y el resto del backend solo las orquesta. **Ninguna clase
de este paquete importa Spring, JPA ni Jackson** — solo `java.*`. Sin Lombok: `record` para value
objects y modelos inmutables.

Clases a crear:

- `Placa` — `record Placa(String valor)` con fábrica `Placa.de(String cruda)`: pasa a mayúsculas,
  elimina espacios y guiones y exige `^[A-Z0-9]{3,10}$`; si no cumple (o es `null`) lanza
  `PlacaInvalidaException`.
- `TipoVehiculo` — enum `OFICIAL`, `RESIDENTE`, `NO_RESIDENTE`.
- `MomentoCobro` — enum `NINGUNO`, `INFORME_MENSUAL`, `AL_SALIR`.
- `cobro/EstrategiaCobro` — interfaz: `TipoVehiculo tipo()`, `MomentoCobro momento()`,
  `BigDecimal calcular(long minutos)`.
- `cobro/CobroOficial` (siempre `0.00`, `NINGUNO`), `cobro/CobroResidente(BigDecimal tarifaPorMinuto)`
  (`INFORME_MENSUAL`), `cobro/CobroNoResidente(BigDecimal tarifaPorMinuto)` (`AL_SALIR`). El importe es
  `tarifa × minutos` con `setScale(2, RoundingMode.HALF_UP)`.
- `cobro/CalculadoraCobro` — recibe `Collection<EstrategiaCobro>`, las indexa por `tipo()` en un
  `EnumMap` y en el constructor lanza `IllegalStateException` si alguna constante de `TipoVehiculo` no
  tiene estrategia (el mensaje nombra el tipo). Expone `calcular(TipoVehiculo, long minutos)` y
  `momento(TipoVehiculo)`. **No contiene `if` ni `switch` sobre el tipo**: añadir un tipo es una
  constante del enum, una clase de estrategia y una tarifa en `application.yml`.
- `Vehiculo`, `Estancia`, `Periodo`, `Acumulado` — modelos del dominio (records con `UUID id` y los
  campos del modelo de datos). `Estancia` expone `static long minutosCompletos(OffsetDateTime entrada,
  OffsetDateTime salida)` = `Duration.between(entrada, salida).toMinutes()` y `boolean abierta()`.
- `excepcion/ExcepcionDominio` — `abstract`, extiende `RuntimeException` y lleva un `String codigo()`.
  Subclases con su código: `PlacaInvalidaException` → `VALIDACION`, `VehiculoYaRegistradoException` →
  `VEHICULO_YA_REGISTRADO`, `VehiculoYaDentroException` → `VEHICULO_YA_DENTRO`,
  `VehiculoNoDentroException` → `VEHICULO_NO_DENTRO`, `VehiculoNoEncontradoException` →
  `VEHICULO_NO_ENCONTRADO`. Los mensajes van en español y son los que verá el operador.

Pruebas (JUnit Jupiter + AssertJ, parametrizadas donde haya tabla de casos):

- `PlacaTest` — `abc-123 d` → `ABC123D`; `AB`, un valor de 11 caracteres, cadena vacía, `null` y
  `ABC<1>` → `PlacaInvalidaException`.
- `EstanciaTest` — 0 s → 0 min; 59 s → 0 min; 10 min 5 s → 10 min.
- `CalculadoraCobroTest` — con tarifas 0.05 y 0.50: 11 min → `0.00` / `0.55` / `5.50` con escala 2;
  0 min → `0.00` para los tres; construir la calculadora sin la estrategia de un tipo lanza
  `IllegalStateException`.

Compara importes con `isEqualByComparingTo` para el valor y afirma `scale()` por separado.

**Files**

- `backend/src/main/java/mx/neology/acceso/domain/**` — new: value objects, enums, modelos, `cobro/`, `excepcion/`
- `backend/src/test/java/mx/neology/acceso/domain/**` — new: `PlacaTest`, `CalculadoraCobroTest`, `EstanciaTest`

**Acceptance**

Copiados literalmente del arreglo `acceptance` de esta tarea en `tasks.json`. Cada uno lo decide un comando de abajo, en esta máquina, durante la construcción.

1. **WHEN** `Placa.de("abc-123 d")` is called **THE SYSTEM SHALL** return a placa whose value is `ABC123D`.
2. **WHEN** `Placa.de` receives `AB`, an 11-character value, an empty string or a value containing `<` **THE SYSTEM SHALL** throw `PlacaInvalidaException`.
3. **WHEN** the time between entrada and salida is 0 seconds, 59 seconds or 10 minutes 5 seconds **THE SYSTEM SHALL** compute 0, 0 and 10 whole minutes respectively.
4. **WHEN** `CalculadoraCobro` computes 11 minutes **THE SYSTEM SHALL** return 0.00 for `OFICIAL`, 0.55 for `RESIDENTE` and 5.50 for `NO_RESIDENTE`, each as a `BigDecimal` with scale 2.
5. **WHEN** `CalculadoraCobro` is constructed without a strategy for some `TipoVehiculo` constant **THE SYSTEM SHALL** throw `IllegalStateException` naming the missing type.
6. **WHEN** the sources under `backend/src/main/java/mx/neology/acceso/domain` are searched for `org.springframework` or `jakarta.persistence` **THE SYSTEM SHALL** find no match.

**Verify** — todos los comandos, en orden, cada uno en su propio shell desde la raíz del proyecto. Cada uno sale con 0 cuando la tarea está bien; la tarea está hecha cuando el último sale con 0.

```bash
(cd backend && ./mvnw -q -B -ntp test -Dtest=PlacaTest,CalculadoraCobroTest,EstanciaTest)   # expect: exit 0, 0 fallos — falla si alguna de las tres clases no existe
grep -rlE "org\.springframework|jakarta\.persistence" backend/src/main/java/mx/neology/acceso/domain; test $? -eq 1   # expect: código 1 = sin coincidencias → la línea sale con 0 (2 sería directorio inexistente y falla)
(cd backend && ./mvnw -q -B -ntp verify)   # expect: exit 0 — formato y todas las pruebas anteriores siguen en verde
```

Son el arreglo `verify` de esta tarea en `tasks.json`, una línea por comando, con la afirmación de código de salida intacta (`...; test $? -eq 1`). No los edites ni los "simplifiques".

**Checkpoint**

```bash
git add -A && git commit -q -m "feat(backend): dominio, value objects y estrategias de cobro [E2-T2]" -m "Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
git tag step-03-dominio
git ls-files --error-unmatch backend/src/main/java/mx/neology/acceso/domain/Placa.java   # expect: exit 0
# punto de retorno si el paso siguiente sale mal: git reset --hard step-03-dominio
```

Ejecuta el bloque después de que el último comando de Verify salga con 0 y antes de empezar la tarea siguiente. La etiqueta es el valor `checkpoint` de esta tarea en `tasks.json`; si esta tarea sale mal, vuelve con `git reset --hard step-02-backend-base`.

### `E2-T3` — Implementar altas de vehículo y manejo de errores

**Depends on:** `E2-T2` · **Priority:** p0 — metadato para recortes de alcance, no un orden de ejecución · **Paso global:** 4

**Objetivo:** Los tres endpoints de alta crean o reclasifican vehículos, y todo error sale como `ProblemDetail` con `code` y `errors`.

Primera rebanada vertical completa: caso de uso → puerto → adaptador JPA → controlador → manejo de
errores. Lo que se decida aquí (puerto de transacción, manejador de errores, estilo de pruebas) lo
reutilizan todos los pasos siguientes del backend.

**Aplicación** (`application`, sin Spring):

- `puerto/VehiculoRepositorio` — `Optional<Vehiculo> buscarPorPlaca(Placa)`, `Vehiculo guardar(Vehiculo)`,
  `long contar()`.
- `puerto/Transaccion` — `<T> T ejecutar(Supplier<T> accion)`. Es la frontera transaccional de los
  casos de uso sin depender de `@Transactional`.
- `RegistrarVehiculo` — `Vehiculo ejecutar(String placaCruda, TipoVehiculo tipo)`. Dentro de
  `transaccion.ejecutar`: normaliza con `Placa.de`; si la placa no existe, crea el vehículo; si existe
  como `NO_RESIDENTE` y el tipo pedido es `OFICIAL` o `RESIDENTE`, lo reclasifica conservando el `id`;
  en cualquier otro caso lanza `VehiculoYaRegistradoException`. Usa el `Clock` inyectado para
  `createdAt`/`updatedAt` y `UUID.randomUUID()` para el id.

**Infraestructura**:

- `persistencia/VehiculoEntity` — `@Entity @Table(name = "vehiculos")` con los campos del modelo de
  datos (`estanciaAbiertaId` como columna `UUID` simple, sin relación mapeada), `VehiculoJpaRepository`
  (`JpaRepository<VehiculoEntity, UUID>` con `findByPlaca`), `VehiculoRepositorioJpa` (adaptador que
  implementa el puerto y convierte entidad ↔ dominio) y `TransaccionSpring` (implementa `Transaccion`
  con `TransactionTemplate`).
- `config/CasosDeUsoConfig` — `@Configuration` con un `@Bean` por caso de uso; los casos de uso no
  llevan anotaciones de Spring.

**Web**:

- `PlacaRequest` — `record PlacaRequest(@NotBlank @Size(min = 3, max = 20) String placa)`.
- `VehiculoResponse` — `record` con `id`, `placa`, `tipo`, `dentro`, `createdAt`.
- `VehiculosController` — `@RestController @RequestMapping("/neo/vehiculos")` con `POST /oficiales`,
  `POST /residentes` y `POST /no-residentes`; cada uno responde
  `ResponseEntity.created(URI.create("/neo/vehiculos/" + placa)).body(...)`.
- `ApiExceptionHandler` — `@RestControllerAdvice` que extiende `ResponseEntityExceptionHandler`.
  Construye siempre un `ProblemDetail` y le añade `setProperty("code", ...)`: `ExcepcionDominio` →
  estado según su `codigo()` (`VALIDACION` 400, `VEHICULO_NO_ENCONTRADO` 404, los tres `VEHICULO_YA_*`
  / `VEHICULO_NO_DENTRO` 409) y, para `PlacaInvalidaException`, `errors = [{field: "placa", message}]`;
  `MethodArgumentNotValidException` y cuerpo ilegible → 400 `VALIDACION` con `errors` por campo;
  cualquier otra excepción → 500 `ERROR_INTERNO` sin traza. Registra cada error con SLF4J en formato
  `clave=valor`.

**Pruebas**:

- `RegistrarVehiculoTest` — unitaria con Mockito sobre los puertos (una implementación de `Transaccion`
  que ejecuta la acción directamente): alta nueva, reclasificación, y los casos de conflicto.
- `VehiculosControllerTest` — `@WebMvcTest(VehiculosController.class)` con el caso de uso como
  `@MockitoBean` (**nunca** `@MockBean`): estado, cabecera `Location`, cuerpo y forma del `ProblemDetail`.
- `VehiculoRepositorioJpaTest` — `@DataJpaTest` contra H2 con Flyway (añade
  `@AutoConfigureTestDatabase(replace = Replace.NONE)` para usar la URL de `application.yml`):
  `buscarPorPlaca("OFI001")` devuelve el vehículo de la precarga; placa duplicada viola `uq_vehiculos_placa`.
- `AltasIntegracionTest` — `@SpringBootTest` + `@AutoConfigureMockMvc` + `@Transactional` (cada prueba
  se revierte, así la precarga queda intacta para las demás): los cinco criterios de aceptación HTTP.

Afirma el JSON con `jsonPath`, no con `ObjectMapper` (en Boot 4 Jackson vive en `tools.jackson.*`).
Las anotaciones de prueba por capas (`@WebMvcTest`, `@DataJpaTest`, `@AutoConfigureMockMvc`,
`@AutoConfigureTestDatabase`) cambiaron de paquete en Boot 4: deja que el compilador o el IDE resuelva
el import desde los starters de prueba que generó Initializr; no copies un import de memoria.

**Files**

- `backend/src/main/java/mx/neology/acceso/application/**` — new: `RegistrarVehiculo`, puertos `VehiculoRepositorio` y `Transaccion`
- `backend/src/main/java/mx/neology/acceso/infrastructure/persistencia/**` — new: `VehiculoEntity`, `VehiculoJpaRepository`, `VehiculoRepositorioJpa`, `TransaccionSpring`
- `backend/src/main/java/mx/neology/acceso/infrastructure/config/CasosDeUsoConfig.java` — new: registra los casos de uso como beans
- `backend/src/main/java/mx/neology/acceso/web/**` — new: `VehiculosController`, `PlacaRequest`, `VehiculoResponse`, `ApiExceptionHandler`
- `backend/src/test/java/mx/neology/acceso/**` — new: `RegistrarVehiculoTest`, `VehiculosControllerTest`, `VehiculoRepositorioJpaTest`, `AltasIntegracionTest`

**Acceptance**

Copiados literalmente del arreglo `acceptance` de esta tarea en `tasks.json`. Cada uno lo decide un comando de abajo, en esta máquina, durante la construcción.

1. **WHEN** `POST /neo/vehiculos/oficiales` receives `{"placa":"abc-123"}` for an unknown placa **THE SYSTEM SHALL** return 201 with the header `Location: /neo/vehiculos/ABC123` and a body with `placa` `ABC123`, `tipo` `OFICIAL` and `dentro` `false`.
2. **WHEN** `POST /neo/vehiculos/residentes` receives a placa already registered as `OFICIAL` or `RESIDENTE` **THE SYSTEM SHALL** return 409 with `code` `VEHICULO_YA_REGISTRADO` and leave the row count of `vehiculos` unchanged.
3. **WHEN** `POST /neo/vehiculos/residentes` receives the placa of an existing `NO_RESIDENTE` vehicle **THE SYSTEM SHALL** return 201, change the `tipo` of that row to `RESIDENTE` and keep its `id`.
4. **WHEN** `POST /neo/vehiculos/no-residentes` receives a placa that already exists with any tipo **THE SYSTEM SHALL** return 409 with `code` `VEHICULO_YA_REGISTRADO`.
5. **WHEN** any of the three alta endpoints receives a placa that is blank, shorter than 3 characters after normalization or contains a character other than letters, digits, spaces and hyphens **THE SYSTEM SHALL** return 400 with content type `application/problem+json`, `code` `VALIDACION` and `errors[0].field` equal to `placa`.
6. **WHEN** the sources under `domain` and `application` are searched for `org.springframework` or `jakarta.persistence` **THE SYSTEM SHALL** find no match.

**Verify** — todos los comandos, en orden, cada uno en su propio shell desde la raíz del proyecto. Cada uno sale con 0 cuando la tarea está bien; la tarea está hecha cuando el último sale con 0.

```bash
(cd backend && ./mvnw -q -B -ntp test -Dtest=RegistrarVehiculoTest,VehiculosControllerTest,VehiculoRepositorioJpaTest,AltasIntegracionTest)   # expect: exit 0, 0 fallos
grep -rlE "org\.springframework|jakarta\.persistence" backend/src/main/java/mx/neology/acceso/domain backend/src/main/java/mx/neology/acceso/application; test $? -eq 1   # expect: código 1 = sin coincidencias → la línea sale con 0
(cd backend && ./mvnw -q -B -ntp verify)   # expect: exit 0
```

Son el arreglo `verify` de esta tarea en `tasks.json`, una línea por comando, con la afirmación de código de salida intacta (`...; test $? -eq 1`). No los edites ni los "simplifiques".

**Checkpoint**

```bash
git add -A && git commit -q -m "feat(backend): altas de vehiculo y ProblemDetail [E2-T3]" -m "Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
git tag step-04-altas-vehiculo
git ls-files --error-unmatch backend/src/main/java/mx/neology/acceso/web/ApiExceptionHandler.java   # expect: exit 0
# punto de retorno si el paso siguiente sale mal: git reset --hard step-04-altas-vehiculo
```

Ejecuta el bloque después de que el último comando de Verify salga con 0 y antes de empezar la tarea siguiente. La etiqueta es el valor `checkpoint` de esta tarea en `tasks.json`; si esta tarea sale mal, vuelve con `git reset --hard step-03-dominio`.

### `E2-T4` — Registrar entradas y salidas con cálculo de cobro

**Depends on:** `E2-T3` · **Priority:** p0 — metadato para recortes de alcance, no un orden de ejecución · **Paso global:** 5

**Objetivo:** Entrada y salida funcionan de extremo a extremo con bloqueo pesimista, alta automática de no residentes, minutos truncados, importe por estrategia y acumulado de residentes.

El núcleo del negocio. Entrada y salida corren cada una en una transacción que bloquea la fila del
vehículo, de modo que dos peticiones simultáneas no pueden abrir dos estancias para la misma placa.

**Aplicación**:

- Puertos nuevos: `EstanciaRepositorio` (`guardar`, `buscarPorId`), `PeriodoRepositorio`
  (`Periodo activo()`), `AcumuladoRepositorio` (`Optional<Acumulado> buscar(UUID vehiculoId, UUID
  periodoId)`, `guardar`). Añade a `VehiculoRepositorio` el método `Optional<Vehiculo>
  buscarPorPlacaParaActualizar(Placa)`.
- `RegistrarEntrada` — sigue el flujo del contrato: normalizar → buscar con bloqueo → crear como
  `NO_RESIDENTE` si no existe → si tiene estancia abierta, `VehiculoYaDentroException` → crear estancia
  con `entrada = OffsetDateTime.now(clock)` → apuntar `estanciaAbiertaId`.
- `RegistrarSalida` — normalizar → buscar con bloqueo → `VehiculoNoEncontradoException` si no existe →
  `VehiculoNoDentroException` si no tiene estancia abierta → `minutos = Estancia.minutosCompletos(...)`
  → `importe = calculadora.calcular(tipo, minutos)` → cerrar la estancia con `periodoId` del periodo
  activo → `estanciaAbiertaId = null` → **si `calculadora.momento(tipo) == MomentoCobro.INFORME_MENSUAL`**,
  sumar minutos e importe al acumulado `(vehículo, periodo activo)`, creándolo si no existe. La
  condición es sobre el momento de cobro que declara la estrategia, nunca sobre el tipo.

**Infraestructura**:

- Entidades `EstanciaEntity`, `PeriodoEntity`, `AcumuladoEntity` con sus repositorios Spring Data y
  adaptadores. Tipos Java: `UUID`, `OffsetDateTime`, `BigDecimal` con `@Column(precision = 12, scale =
  2)`, `Integer` para `minutos`, `Boolean` (envoltorio) para `activo`.
- En `VehiculoJpaRepository`: `@Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select v from
  VehiculoEntity v where v.placa = :placa") Optional<VehiculoEntity> buscarParaActualizar(String placa)`.
- En `config/`: beans de `CobroOficial`, `CobroResidente` y `CobroNoResidente` construidos con
  `TarifasProperties`, el bean `CalculadoraCobro` con la lista de estrategias, y los beans de los dos
  casos de uso.

**Web**: `EstanciasController` (`POST /neo/estancias/entrada` → 201, `POST /neo/estancias/salida` →
200) y `EstanciaResponse` con `id`, `placa`, `tipo`, `entrada`, `salida`, `minutos`, `importe`,
`momentoCobro`, `periodoId`.

**Pruebas**:

- `soporte/RelojDePrueba` — subclase de `Clock` con instante ajustable (`fijar(Instant)`,
  `avanzar(Duration)`), zona `America/Mexico_City`, registrada con un `@TestConfiguration` que la
  expone como `@Bean @Primary Clock`. Las pruebas de integración la importan con `@Import`.
- `RegistrarEntradaTest` y `RegistrarSalidaTest` — unitarias con Mockito y `Clock.fixed`: todas las
  ramas, incluido que un `OFICIAL` sale con `0.00` y `NINGUNO` sin tocar acumulados.
- `EstanciasIntegracionTest` — `@SpringBootTest` + `@AutoConfigureMockMvc` + `@Transactional`: los
  seis criterios de aceptación; avanza el reloj 10 min 5 s y 11 min entre entrada y salida.

Afirma fechas parseando con `OffsetDateTime.parse` y comparando con `isEqual`, no comparando cadenas.
Una prueba de concurrencia con hilos **no es obligatoria** en este paso: la garantía la dan el bloqueo
y la restricción `uq_vehiculos_estancia_abierta`.

**Files**

- `backend/src/main/java/mx/neology/acceso/application/**` — new: `RegistrarEntrada`, `RegistrarSalida`, puertos `EstanciaRepositorio`, `PeriodoRepositorio`, `AcumuladoRepositorio`
- `backend/src/main/java/mx/neology/acceso/infrastructure/persistencia/**` — new: entidades, repositorios y adaptadores de estancia, periodo y acumulado; edit: consulta con bloqueo en `VehiculoJpaRepository`
- `backend/src/main/java/mx/neology/acceso/infrastructure/config/**` — edit: beans de estrategias, `CalculadoraCobro` y los dos casos de uso
- `backend/src/main/java/mx/neology/acceso/web/**` — new: `EstanciasController`, `EstanciaResponse`; edit: `ApiExceptionHandler`
- `backend/src/test/java/mx/neology/acceso/**` — new: `RegistrarEntradaTest`, `RegistrarSalidaTest`, `EstanciasIntegracionTest`, `soporte/RelojDePrueba`

**Acceptance**

Copiados literalmente del arreglo `acceptance` de esta tarea en `tasks.json`. Cada uno lo decide un comando de abajo, en esta máquina, durante la construcción.

1. **WHEN** `POST /neo/estancias/entrada` receives a placa that does not exist **THE SYSTEM SHALL** return 201, create the vehicle with `tipo` `NO_RESIDENTE` and return an estancia whose `salida`, `minutos` and `importe` are `null`.
2. **WHEN** `POST /neo/estancias/entrada` receives a placa that already has an open estancia **THE SYSTEM SHALL** return 409 with `code` `VEHICULO_YA_DENTRO` and insert no row into `estancias`.
3. **WHEN** `POST /neo/estancias/salida` is received 10 minutes and 5 seconds after the entrada of a `NO_RESIDENTE` vehicle **THE SYSTEM SHALL** return 200 with `minutos` 10, `importe` 5.00 and `momentoCobro` `AL_SALIR`, and set `estancia_abierta_id` of the vehicle to null.
4. **WHEN** `POST /neo/estancias/salida` closes an 11-minute estancia of a `RESIDENTE` vehicle **THE SYSTEM SHALL** return `importe` 0.55 and `momentoCobro` `INFORME_MENSUAL` and add 11 minutes and 0.55 to the row of that vehicle in `acumulados_residentes` for the active period.
5. **WHEN** `POST /neo/estancias/salida` receives a placa that has no open estancia **THE SYSTEM SHALL** return 409 with `code` `VEHICULO_NO_DENTRO`.
6. **WHEN** `POST /neo/estancias/salida` receives a placa that does not exist **THE SYSTEM SHALL** return 404 with `code` `VEHICULO_NO_ENCONTRADO`.

**Verify** — todos los comandos, en orden, cada uno en su propio shell desde la raíz del proyecto. Cada uno sale con 0 cuando la tarea está bien; la tarea está hecha cuando el último sale con 0.

```bash
(cd backend && ./mvnw -q -B -ntp test -Dtest=RegistrarEntradaTest,RegistrarSalidaTest,EstanciasIntegracionTest)   # expect: exit 0, 0 fallos
grep -rlE "org\.springframework|jakarta\.persistence" backend/src/main/java/mx/neology/acceso/domain backend/src/main/java/mx/neology/acceso/application; test $? -eq 1   # expect: código 1 = sin coincidencias → la línea sale con 0
(cd backend && ./mvnw -q -B -ntp verify)   # expect: exit 0
```

Son el arreglo `verify` de esta tarea en `tasks.json`, una línea por comando, con la afirmación de código de salida intacta (`...; test $? -eq 1`). No los edites ni los "simplifiques".

**Checkpoint**

```bash
git add -A && git commit -q -m "feat(backend): entrada y salida de estancias con calculo de cobro [E2-T4]" -m "Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
git tag step-05-entrada-salida
git ls-files --error-unmatch backend/src/main/java/mx/neology/acceso/web/EstanciasController.java   # expect: exit 0
# punto de retorno si el paso siguiente sale mal: git reset --hard step-05-entrada-salida
```

Ejecuta el bloque después de que el último comando de Verify salga con 0 y antes de empezar la tarea siguiente. La etiqueta es el valor `checkpoint` de esta tarea en `tasks.json`; si esta tarea sale mal, vuelve con `git reset --hard step-04-altas-vehiculo`.

### `E2-T5` — Implementar inicio de mes y archivo de periodos

**Depends on:** `E2-T4` · **Priority:** p0 — metadato para recortes de alcance, no un orden de ejecución · **Paso global:** 6

**Objetivo:** `POST /neo/mes/iniciar` cierra el periodo activo y abre uno nuevo sin borrar estancias ni acumulados.

"Reiniciar el mes" no borra nada: cierra el periodo activo y abre uno nuevo. Los acumulados del
periodo nuevo empiezan en cero porque todavía no existe ninguna fila para él, y las estancias
cerradas conservan el `periodo_id` con el que se archivaron.

- `application/IniciarMes` — dentro de `transaccion.ejecutar`: obtiene el periodo activo, lo cierra con
  `fin = OffsetDateTime.now(clock)`, y abre uno nuevo con `inicio` igual a ese mismo instante. Devuelve
  un resultado con `periodoAnterior` y `periodoActual`. Añade a `PeriodoRepositorio` los métodos
  `cerrar(Periodo)` y `abrir(Periodo)`.
- Adaptador de periodos — `cerrar` pone `activo = null` y `fin`, y **hace `saveAndFlush`** antes de que
  `abrir` inserte la fila con `activo = true`: la restricción `uq_periodos_activo` no admite dos `TRUE`
  ni siquiera dentro de la misma transacción. Registra el bean del caso de uso en
  `config/CasosDeUsoConfig`.
- `web/MesController` — `POST /neo/mes/iniciar` → 200 con `IniciarMesResponse(periodoAnterior,
  periodoActual)`; `PeriodoResponse` lleva `id`, `inicio`, `fin`.
- Log: `evento=mes_iniciado periodo_anterior=<id> periodo_actual=<id>`.

Pruebas:

- `IniciarMesTest` — unitaria con Mockito: se cierra el activo con la hora del reloj y se abre el nuevo.
- `MesIntegracionTest` — `@SpringBootTest` + `@AutoConfigureMockMvc` + `@Transactional` con
  `RelojDePrueba`: (a) tras iniciar mes queda exactamente 1 fila con `activo = true`; (b) el número de
  filas de `estancias` y de `acumulados_residentes` no cambia y las estancias cerradas conservan su
  `periodo_id`; (c) `RES001` registra entrada y salida después del cambio y aparece una fila nueva de
  acumulado para el periodo nuevo con solo esos minutos, mientras la fila del periodo anterior sigue
  con 210 minutos; (d) `OFI002`, que estaba dentro desde antes, sale después del cambio y su estancia
  queda con el `periodo_id` del periodo nuevo; (e) insertar con `JdbcTemplate` una segunda fila con
  `activo = true` lanza `DataIntegrityViolationException`.

No existe operación para "deshacer" el inicio de mes; la confirmación previa es responsabilidad del
frontend.

**Files**

- `backend/src/main/java/mx/neology/acceso/application/**` — new: `IniciarMes`
- `backend/src/main/java/mx/neology/acceso/infrastructure/**` — edit: adaptador de periodos (cerrar con flush, abrir) y bean del caso de uso
- `backend/src/main/java/mx/neology/acceso/web/**` — new: `MesController`, `PeriodoResponse`, `IniciarMesResponse`
- `backend/src/test/java/mx/neology/acceso/**` — new: `IniciarMesTest`, `MesIntegracionTest`

**Acceptance**

Copiados literalmente del arreglo `acceptance` de esta tarea en `tasks.json`. Cada uno lo decide un comando de abajo, en esta máquina, durante la construcción.

1. **WHEN** `POST /neo/mes/iniciar` is called **THE SYSTEM SHALL** return 200 with `periodoAnterior.fin` set to the current server time and `periodoActual.fin` equal to `null`, and leave exactly 1 row of `periodos` with `activo` true.
2. **WHEN** a month is started **THE SYSTEM SHALL** keep the `periodo_id` of every closed estancia unchanged and delete no row from `estancias` or `acumulados_residentes`.
3. **WHEN** a `RESIDENTE` vehicle with accumulated minutes in the previous period closes an estancia after the month is started **THE SYSTEM SHALL** create a new `acumulados_residentes` row for the new period holding only the minutes and importe of that estancia.
4. **WHEN** a vehicle whose estancia was opened before the month was started registers its salida afterwards **THE SYSTEM SHALL** set the `periodo_id` of that estancia to the new active period.
5. **WHEN** a second row with `activo` true is inserted directly into `periodos` **THE SYSTEM SHALL** reject it with a violation of the unique constraint `uq_periodos_activo`.

**Verify** — todos los comandos, en orden, cada uno en su propio shell desde la raíz del proyecto. Cada uno sale con 0 cuando la tarea está bien; la tarea está hecha cuando el último sale con 0.

```bash
(cd backend && ./mvnw -q -B -ntp test -Dtest=IniciarMesTest,MesIntegracionTest)   # expect: exit 0, 0 fallos
grep -rlE "org\.springframework|jakarta\.persistence" backend/src/main/java/mx/neology/acceso/domain backend/src/main/java/mx/neology/acceso/application; test $? -eq 1   # expect: código 1 = sin coincidencias → la línea sale con 0
(cd backend && ./mvnw -q -B -ntp verify)   # expect: exit 0
```

Son el arreglo `verify` de esta tarea en `tasks.json`, una línea por comando, con la afirmación de código de salida intacta (`...; test $? -eq 1`). No los edites ni los "simplifiques".

**Checkpoint**

```bash
git add -A && git commit -q -m "feat(backend): inicio de mes y archivo de periodos [E2-T5]" -m "Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
git tag step-06-iniciar-mes
git ls-files --error-unmatch backend/src/main/java/mx/neology/acceso/web/MesController.java   # expect: exit 0
# punto de retorno si el paso siguiente sale mal: git reset --hard step-06-iniciar-mes
```

Ejecuta el bloque después de que el último comando de Verify salga con 0 y antes de empezar la tarea siguiente. La etiqueta es el valor `checkpoint` de esta tarea en `tasks.json`; si esta tarea sale mal, vuelve con `git reset --hard step-05-entrada-salida`.

### `E2-T6` — Generar informe de pagos en JSON y Excel

**Depends on:** `E2-T4` · **Priority:** p0 — metadato para recortes de alcance, no un orden de ejecución · **Paso global:** 7

**Objetivo:** `GET /neo/residentes/pagos` entrega el informe del periodo activo como JSON o como `.xlsx` según la cabecera `Accept`.

Un solo caso de uso de lectura con dos representaciones. El cálculo vive en `application`; Apache POI
vive únicamente en `infrastructure.excel`.

- `application/GenerarInformePagos` — devuelve `InformePagos(periodo, filas, totalMinutos,
  totalImporte)`: una fila `(placa, minutos, importe)` por cada vehículo `RESIDENTE`, ordenadas por
  placa ascendente, con el acumulado del periodo activo o `0` / `0.00` si no tiene fila. Los totales
  son la suma de las filas. Añade al puerto que corresponda una consulta que traiga los residentes con
  su acumulado del periodo en **una** consulta (sin N+1).
- `infrastructure/excel/InformeExcelWriter` — `byte[] escribir(InformePagos)` con `XSSFWorkbook`
  dentro de un `try-with-resources`: hoja `Pagos`; fila 1 con las cabeceras `Placa`, `Minutos`,
  `Importe` en negrita; una fila por residente; última fila con `TOTAL`, el total de minutos y el total
  de importe. Minutos e importe se escriben como celdas numéricas (`setCellValue(double)`), con formato
  `0.00` para el importe.
- `web/ResidentesController` — dos métodos sobre `GET /neo/residentes/pagos`: uno con `produces =
  MediaType.APPLICATION_JSON_VALUE` que devuelve `InformePagosResponse`, y otro con `produces =
  "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"` que devuelve
  `ResponseEntity<byte[]>`. La cabecera de descarga se escribe **como cadena literal**:
  `"attachment; filename=\"informe-pagos-" + mes + ".xlsx\""`, donde `mes` es `periodo.inicio`
  formateado con el patrón `yyyy-MM` en la zona `America/Mexico_City`. No uses el constructor
  `ContentDisposition` de Spring: la cadena exacta es parte del contrato que afirma la prueba.

Pruebas:

- `GenerarInformePagosTest` — unitaria: orden por placa, residente sin acumulado con ceros, exclusión
  de oficiales y no residentes, totales.
- `InformeExcelWriterTest` — escribe un informe de ejemplo y lo vuelve a leer con `new
  XSSFWorkbook(new ByteArrayInputStream(bytes))`: nombre de hoja, cabeceras, tipo numérico de las
  celdas y fila `TOTAL`.
- `InformeIntegracionTest` — `@SpringBootTest` + `@AutoConfigureMockMvc` + `@Transactional` contra la
  precarga: JSON con `Accept: application/json` (3 filas, 300, 15.00); Excel con el `Accept` de hoja de
  cálculo (tipo de contenido, cabecera `Content-Disposition`, y el contenido releído con POI: primera
  fila de cabeceras, una fila por residente y última fila `TOTAL` con 300 y 15.00). Añade un residente
  nuevo sin estancias y comprueba que sale con ceros.

Compara los importes leídos del Excel con una tolerancia (`isCloseTo(15.00, within(0.001))`): son
`double` en la celda.

**Files**

- `backend/src/main/java/mx/neology/acceso/application/**` — new: `GenerarInformePagos` y su modelo de resultado
- `backend/src/main/java/mx/neology/acceso/infrastructure/**` — new: `excel/InformeExcelWriter`; edit: consulta de residentes con su acumulado y bean del caso de uso
- `backend/src/main/java/mx/neology/acceso/web/**` — new: `ResidentesController`, `InformePagosResponse`
- `backend/src/test/java/mx/neology/acceso/**` — new: `GenerarInformePagosTest`, `InformeExcelWriterTest`, `InformeIntegracionTest`

**Acceptance**

Copiados literalmente del arreglo `acceptance` de esta tarea en `tasks.json`. Cada uno lo decide un comando de abajo, en esta máquina, durante la construcción.

1. **WHEN** `GET /neo/residentes/pagos` is requested with `Accept: application/json` against the preloaded database **THE SYSTEM SHALL** return 200 with 3 `filas` ordered `RES001`, `RES002`, `RES003`, `totalMinutos` 300 and `totalImporte` 15.00.
2. **WHEN** `GET /neo/residentes/pagos` is requested with `Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` **THE SYSTEM SHALL** return 200 with that content type and the header `Content-Disposition: attachment; filename="informe-pagos-2026-09.xlsx"`.
3. **WHEN** the returned `.xlsx` is opened with Apache POI **THE SYSTEM SHALL** contain a sheet named `Pagos` whose first row is `Placa`, `Minutos`, `Importe`, followed by one row per resident and a last row `TOTAL` with 300 and 15.00 as numeric cells.
4. **WHEN** a `RESIDENTE` vehicle has no `acumulados_residentes` row in the active period **THE SYSTEM SHALL** list it with `minutos` 0 and `importe` 0.00.
5. **WHEN** vehicles of tipo `OFICIAL` or `NO_RESIDENTE` exist **THE SYSTEM SHALL** exclude them from `filas`.

**Verify** — todos los comandos, en orden, cada uno en su propio shell desde la raíz del proyecto. Cada uno sale con 0 cuando la tarea está bien; la tarea está hecha cuando el último sale con 0.

```bash
(cd backend && ./mvnw -q -B -ntp test -Dtest=GenerarInformePagosTest,InformeExcelWriterTest,InformeIntegracionTest)   # expect: exit 0, 0 fallos
grep -rlE "org\.springframework|jakarta\.persistence|org\.apache\.poi" backend/src/main/java/mx/neology/acceso/domain backend/src/main/java/mx/neology/acceso/application; test $? -eq 1   # expect: código 1 = sin coincidencias → la línea sale con 0 (POI solo vive en infrastructure)
(cd backend && ./mvnw -q -B -ntp verify)   # expect: exit 0
```

Son el arreglo `verify` de esta tarea en `tasks.json`, una línea por comando, con la afirmación de código de salida intacta (`...; test $? -eq 1`). No los edites ni los "simplifiques".

**Checkpoint**

```bash
git add -A && git commit -q -m "feat(backend): informe de pagos de residentes en JSON y Excel [E2-T6]" -m "Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
git tag step-07-informe-pagos
git ls-files --error-unmatch backend/src/main/java/mx/neology/acceso/infrastructure/excel/InformeExcelWriter.java   # expect: exit 0
# punto de retorno si el paso siguiente sale mal: git reset --hard step-07-informe-pagos
```

Ejecuta el bloque después de que el último comando de Verify salga con 0 y antes de empezar la tarea siguiente. La etiqueta es el valor `checkpoint` de esta tarea en `tasks.json`; si esta tarea sale mal, vuelve con `git reset --hard step-06-iniciar-mes`.

### `E2-T7` — Exponer listado y detalle y fijar calidad backend

**Depends on:** `E2-T5`, `E2-T6` · **Priority:** p0 — metadato para recortes de alcance, no un orden de ejecución · **Paso global:** 8

**Objetivo:** Existen los tres endpoints de consulta con paginación, orden y filtro en base de datos, la documentación OpenAPI cubre las 10 rutas y JaCoCo exige 80 % de líneas en `domain` y `application`.

Cierra el backend: las tres consultas que necesita el frontend, la documentación OpenAPI y la puerta
de cobertura. Paginación, orden y filtro se resuelven **en la consulta**, nunca cargando todo y
cortando en memoria.

- `application/modelo/ConsultaPagina` — `record(int pagina, int tamano, String campoOrden, boolean
  ascendente)`; `application/modelo/Pagina<T>` — `record(List<T> contenido, int pagina, int tamano,
  long totalElementos, int totalPaginas)`. Son tipos propios para que `application` no importe
  `Pageable` de Spring Data; el adaptador los traduce a `PageRequest`.
- `application/ConsultarVehiculos` — `listar(String filtroPlaca, ConsultaPagina)`, `detalle(String
  placa)` (vehículo + estancia abierta + acumulado del periodo activo si `calculadora.momento(tipo) ==
  INFORME_MENSUAL`) y `estancias(String placa, ConsultaPagina)`; las dos últimas lanzan
  `VehiculoNoEncontradoException` si la placa no existe.
- Adaptadores — consulta paginada de vehículos con filtro `placa like %:filtro%` sobre la placa
  normalizada, y estancias de un vehículo ordenadas por `entrada desc`.
- `web` — `PageResponse<T>(content, page, size, totalElements, totalPages)`, `VehiculoResumen`,
  `VehiculoDetalleResponse`. En `VehiculosController` añade `GET /neo/vehiculos`, `GET
  /neo/vehiculos/{placa}` y `GET /neo/vehiculos/{placa}/estancias`. El controlador valida los
  parámetros: `page` por defecto 0 y `size` por defecto 10; `size` mayor que 100 se recorta a 100;
  `page < 0`, `size < 1`, un campo de `sort` fuera de `placa`, `tipo`, `createdAt` o una dirección
  distinta de `asc`/`desc` lanzan una excepción que `ApiExceptionHandler` convierte en 400
  `PARAMETRO_INVALIDO`. Orden por defecto: `placa,asc`.
- OpenAPI — `@Tag` por controlador y `@Operation(summary = "...")` en español en cada endpoint.
- `backend/pom.xml` — añade dentro de `<build><plugins>`:

```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.15</version>
  <executions>
    <execution>
      <id>jacoco-prepare-agent</id>
      <goals>
        <goal>prepare-agent</goal>
      </goals>
    </execution>
    <execution>
      <id>jacoco-report</id>
      <phase>verify</phase>
      <goals>
        <goal>report</goal>
      </goals>
    </execution>
    <execution>
      <id>jacoco-check</id>
      <phase>verify</phase>
      <goals>
        <goal>check</goal>
      </goals>
      <configuration>
        <includes>
          <include>mx/neology/acceso/domain/**</include>
          <include>mx/neology/acceso/application/**</include>
        </includes>
        <rules>
          <rule>
            <element>BUNDLE</element>
            <limits>
              <limit>
                <counter>LINE</counter>
                <value>COVEREDRATIO</value>
                <minimum>0.80</minimum>
              </limit>
            </limits>
          </rule>
        </rules>
      </configuration>
    </execution>
  </executions>
</plugin>
```

Pruebas:

- `ConsultarVehiculosTest` — unitaria con Mockito: detalle de residente con y sin acumulado, detalle
  de no residente sin acumulado, placa inexistente.
- `ConsultasIntegracionTest` — `@SpringBootTest` + `@AutoConfigureMockMvc` contra la precarga (solo
  lectura): los cinco primeros criterios de aceptación.
- `OpenApiDocsTest` — `@SpringBootTest` + `@AutoConfigureMockMvc`: `GET /v3/api-docs` → 200 y
  `jsonPath("$.paths['<ruta>']").exists()` para cada una de estas 10 rutas: `/neo/vehiculos/oficiales`,
  `/neo/vehiculos/residentes`, `/neo/vehiculos/no-residentes`, `/neo/estancias/entrada`,
  `/neo/estancias/salida`, `/neo/residentes/pagos`, `/neo/mes/iniciar`, `/neo/vehiculos`,
  `/neo/vehiculos/{placa}`, `/neo/vehiculos/{placa}/estancias`.

La regla de cobertura llega en este paso y no antes porque es el paso que termina `domain` y
`application`. Si `jacoco:check` queda por debajo de 0.80, añade las pruebas unitarias que falten
sobre esos dos paquetes; **no bajes el umbral ni añadas exclusiones**.

**Files**

- `backend/src/main/java/mx/neology/acceso/application/**` — new: `ConsultarVehiculos`, `modelo/Pagina`, `modelo/ConsultaPagina`
- `backend/src/main/java/mx/neology/acceso/infrastructure/**` — edit: consultas paginadas en adaptadores y repositorios; bean del caso de uso
- `backend/src/main/java/mx/neology/acceso/web/**` — new: `PageResponse`, `VehiculoResumen`, `VehiculoDetalleResponse`; edit: `VehiculosController` y anotaciones `@Tag`/`@Operation` en todos los controladores
- `backend/pom.xml` — edit: plugin JaCoCo con `prepare-agent`, `report` y `check`
- `backend/src/test/java/mx/neology/acceso/**` — new: `ConsultarVehiculosTest`, `ConsultasIntegracionTest`, `OpenApiDocsTest`

**Acceptance**

Copiados literalmente del arreglo `acceptance` de esta tarea en `tasks.json`. Cada uno lo decide un comando de abajo, en esta máquina, durante la construcción.

1. **WHEN** `GET /neo/vehiculos?page=0&size=2&sort=placa,asc` is requested against the preloaded database **THE SYSTEM SHALL** return 200 with `content` holding `NOR001` and `OFI001` in that order, `totalElements` 6 and `totalPages` 3.
2. **WHEN** `GET /neo/vehiculos?placa=res` is requested against the preloaded database **THE SYSTEM SHALL** return exactly the vehicles `RES001`, `RES002` and `RES003`, filtered by the database query.
3. **WHEN** `GET /neo/vehiculos` receives `size=500` **THE SYSTEM SHALL** answer with `size` 100, and when it receives `sort=color,asc`, `page=-1` or `size=0` THE SYSTEM SHALL return 400 with `code` `PARAMETRO_INVALIDO`.
4. **WHEN** `GET /neo/vehiculos/RES001` is requested against the preloaded database **THE SYSTEM SHALL** return `tipo` `RESIDENTE`, `dentro` `false` and `acumuladoPeriodo` with `minutos` 210 and `importe` 10.50, and for the unknown placa `ZZZ999` THE SYSTEM SHALL return 404 with `code` `VEHICULO_NO_ENCONTRADO`.
5. **WHEN** `GET /neo/vehiculos/RES001/estancias` is requested against the preloaded database **THE SYSTEM SHALL** return its 2 estancias with the one entered on 2026-09-03 before the one entered on 2026-09-02.
6. **WHEN** `./mvnw -q -B -ntp verify` runs in `backend/` **THE SYSTEM SHALL** exit 0 with the JaCoCo `check` rule requiring a line-coverage ratio of at least 0.80 over `mx.neology.acceso.domain` and `mx.neology.acceso.application`, and with `OpenApiDocsTest` finding each of the 10 `/neo` routes in `/v3/api-docs`.

**Verify** — todos los comandos, en orden, cada uno en su propio shell desde la raíz del proyecto. Cada uno sale con 0 cuando la tarea está bien; la tarea está hecha cuando el último sale con 0.

```bash
(cd backend && ./mvnw -q -B -ntp test -Dtest=ConsultarVehiculosTest,ConsultasIntegracionTest,OpenApiDocsTest)   # expect: exit 0, 0 fallos
grep -rlE "org\.springframework|jakarta\.persistence|org\.apache\.poi" backend/src/main/java/mx/neology/acceso/domain backend/src/main/java/mx/neology/acceso/application; test $? -eq 1   # expect: código 1 = sin coincidencias → la línea sale con 0
grep -q "jacoco-maven-plugin" backend/pom.xml   # expect: exit 0
(cd backend && ./mvnw -q -B -ntp verify)   # expect: exit 0 — incluye spotless:check y jacoco:check (≥ 0.80)
test -f backend/target/site/jacoco/index.html   # expect: exit 0 — el informe de cobertura se generó en la ruta por defecto del goal `report`
```

Son el arreglo `verify` de esta tarea en `tasks.json`, una línea por comando, con la afirmación de código de salida intacta (`...; test $? -eq 1`). No los edites ni los "simplifiques".

**Checkpoint**

```bash
git add -A && git commit -q -m "feat(backend): listado, detalle, historial, OpenAPI y cobertura JaCoCo [E2-T7]" -m "Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
git tag step-08-consultas-calidad
git ls-files --error-unmatch backend/src/main/java/mx/neology/acceso/web/PageResponse.java   # expect: exit 0
# punto de retorno si el paso siguiente sale mal: git reset --hard step-08-consultas-calidad
```

Ejecuta el bloque después de que el último comando de Verify salga con 0 y antes de empezar la tarea siguiente. La etiqueta es el valor `checkpoint` de esta tarea en `tasks.json`; si esta tarea sale mal, vuelve con `git reset --hard step-07-informe-pagos`.

---

## Epic acceptance

The epic is done when every task is `done` **and**:

1. **WHEN** `./mvnw -q -B -ntp verify` runs in `backend/` **THE SYSTEM SHALL** exit 0 with all tests passing, `spotless:check` clean and `jacoco:check` at or above 0.80 line coverage over `domain` and `application`.
2. **WHEN** `OpenApiDocsTest` requests `/v3/api-docs` **THE SYSTEM SHALL** find the 10 `/neo` routes, and **WHEN** the sources under `domain` and `application` are searched for Spring, JPA or POI imports **THE SYSTEM SHALL** find no match.

```bash
(cd backend && ./mvnw -q -B -ntp verify)
grep -rlE "org\.springframework|jakarta\.persistence|org\.apache\.poi" backend/src/main/java/mx/neology/acceso/domain backend/src/main/java/mx/neology/acceso/application; test $? -eq 1
```

Run from the project root. Both criteria must be decidable by these commands.

## Pitfalls

- **Importar `Pageable` o `@Transactional` en `application`** — rompe el `grep` de pureza; usa
  `ConsultaPagina`/`Pagina<T>` y el puerto `Transaccion`.
- **`Schema-validation` al arrancar** — una entidad no coincide con la migración: corrige la
  anotación de la entidad (tipo, `precision`/`scale`, `length`), nunca el SQL confirmado.
- **Dos periodos activos** — al iniciar mes, `saveAndFlush` del cierre antes de insertar el nuevo;
  `uq_periodos_activo` no admite dos `TRUE` ni en la misma transacción.
- **Precarga contaminada** — una prueba de integración sin `@Transactional` que escribe deja 7
  vehículos y `BaseDatosMigradaTest` falla después. Solo `ArranqueHttpTest` (HTTP real) no revierte,
  y por eso es de solo lectura.
- **Comparar fechas como cadenas** — parsea con `OffsetDateTime.parse` y compara con `isEqual`.
- **Construir `Content-Disposition` con `ContentDisposition.attachment()`** — la prueba afirma la
  cadena literal; escríbela a mano.
- **`-Dsurefire.failIfNoSpecifiedTests=false`** — haría pasar un `-Dtest=` con clases inexistentes;
  no se usa.

## Before moving on

- [ ] Every task in this epic is `done` in `tasks.json` — no task left `in_progress`.
- [ ] Every `verify` command of every task in this epic passed, not just the first one.
- [ ] No `verify` command was edited, and none was skipped because a file it names did not exist.
- [ ] **Every task in this epic has its `checkpoint` tag in version control** — one tag per task,
      matching the `checkpoint` value in `tasks.json`. `git tag -l 'step-*'` lists them.
- [ ] Gate command passes clean, run from the project root.
- [ ] Every "Produced" contract above exists with the stated signature.
- [ ] No file outside the subtree was modified.
- [ ] This project has no `.env.example`: no environment variable was added.
- [ ] One commit per task, each carrying its task id, each followed by its checkpoint tag.
