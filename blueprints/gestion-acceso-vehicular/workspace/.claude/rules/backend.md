---
description: Convenciones del backend Spring Boot (capas, persistencia, errores y pruebas)
paths:
  - "backend/**"
---

# Backend — convenciones

## Capas (`mx.neology.acceso`)

- `domain` y `application` son Java puro: ni `org.springframework`, ni `jakarta.persistence`, ni POI.
  Los casos de uso son clases sin anotaciones; se registran como beans en
  `infrastructure/config/CasosDeUsoConfig`.
- La frontera transaccional es el puerto `application.puerto.Transaccion`
  (`transaccion.ejecutar(() -> ...)`), implementado con `TransactionTemplate`. No uses
  `@Transactional` en casos de uso.
- `web` solo habla con casos de uso. Una entidad JPA nunca sale de `infrastructure.persistencia`;
  una respuesta HTTP siempre es un `record` de `web`.
- Añadir un tipo de vehículo = constante en `TipoVehiculo` + una clase `EstrategiaCobro` + su bean +
  su tarifa en `application.yml` + una migración que amplíe `ck_vehiculos_tipo`. Nada más cambia.

## Persistencia

- Flyway es el único dueño del esquema; `spring.jpa.hibernate.ddl-auto=validate`. Nunca edites una
  migración confirmada: crea `V<n+1>__<descripcion>.sql`.
- Tipos: `UUID` (generado con `UUID.randomUUID()`), `OffsetDateTime` para `TIMESTAMP WITH TIME ZONE`,
  `BigDecimal` con `@Column(precision = 12, scale = 2)`, `@Enumerated(EnumType.STRING)`.
- Entrada y salida leen el vehículo con `@Lock(LockModeType.PESSIMISTIC_WRITE)`.
- `periodos.activo` es `TRUE` o `NULL`, nunca `FALSE`. Al cerrar un periodo haz `saveAndFlush` antes
  de insertar el nuevo (`uq_periodos_activo`).
- Paginación, orden y filtro van en la consulta. `application` usa sus tipos `ConsultaPagina` y
  `Pagina<T>`; solo el adaptador conoce `Pageable`.

## Web y errores

- Rutas literales del enunciado bajo `/neo`; sin prefijo de versión.
- Todo error es un `ProblemDetail` construido en `ApiExceptionHandler` con la propiedad `code`
  (`VALIDACION`, `PARAMETRO_INVALIDO`, `VEHICULO_NO_ENCONTRADO`, `VEHICULO_YA_REGISTRADO`,
  `VEHICULO_YA_DENTRO`, `VEHICULO_NO_DENTRO`, `ERROR_INTERNO`) y, en validación, `errors`.
- Las excepciones de negocio extienden `ExcepcionDominio` y llevan su `codigo()`; los controladores
  no capturan excepciones.
- Log con SLF4J en formato `clave=valor` (`evento=salida placa=ABC123 minutos=10 importe=5.00`).

## Spring Boot 4

- Starter web: `spring-boot-starter-webmvc`. Conserva los starters de prueba que generó Initializr.
- Pruebas: `@MockitoBean`, nunca `@MockBean`. Jackson 3 vive en `tools.jackson.*`: afirma JSON con
  `jsonPath` y evita `ObjectMapper`.
- Las anotaciones de prueba por capas (`@WebMvcTest`, `@DataJpaTest`, `@AutoConfigureMockMvc`,
  `@AutoConfigureTestDatabase`) cambiaron de paquete respecto a Boot 3: deja que el compilador
  resuelva el import, no lo copies de memoria.

## Pruebas

- JUnit Jupiter + AssertJ + Mockito. Nombre de prueba = comportamiento (`rechazaSalidaSinEntrada`).
- Unitarias para `domain` y `application` (puertos simulados, `Clock.fixed`).
- Integración con `@SpringBootTest` + `@AutoConfigureMockMvc`; si la prueba escribe, añade
  `@Transactional` para que se revierta y la precarga quede intacta.
- `@DataJpaTest` con `@AutoConfigureTestDatabase(replace = Replace.NONE)` para usar H2 + Flyway.
- Tiempo en integración: `soporte/RelojDePrueba` importado con `@Import`.
- Importes: `isEqualByComparingTo`; fechas: `OffsetDateTime.parse(...)` + `isEqual`, no cadenas.
- Antes de verificar: `(cd backend && ./mvnw -q -B -ntp spotless:apply)`.
- Cobertura: JaCoCo exige 0.80 de líneas en `domain` + `application`. No bajes el umbral.
