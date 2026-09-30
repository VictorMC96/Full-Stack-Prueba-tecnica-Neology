# Caso Práctico – Gestión de Acceso Vehicular

Este repositorio contiene una serie de requerimientos de un Caso Práctico, que busca evaluar las capacidades técnicas del candidato con respecto a las principales funciones y responsabilidades que se requieren dentro del área de Desarrollo de Software de Neology.

## ¿Qué se busca evaluar?

Principalmente los siguientes aspectos:

- Creatividad para resolver los requerimientos.
- Uso de experiencia y conocimiento con base en buenas prácticas.
- Eficiencia de los algoritmos entregados.
- Mostrar la experiencia y la manera de salir de la caja para romper o identificar cualquier falla o mejora.
- Familiaridad con frameworks y plataformas.

## Consideraciones al finalizar la prueba

- Enviar la prueba técnica al correo de la consultora, con copia a `vmiranda@neology.mx` y `lluna@neopartners.mx`.
- Cumplir con los puntos que se solicitan, haciendo énfasis en el detalle de las interfaces.
- Clonar el proyecto y subirlo como una rama adicional.

## Instrucciones

- **Tiempo estimado:** 5 horas.
- Se espera que completes las tareas especificadas a continuación utilizando **Angular** y **Spring Boot**.
- La prueba se evaluará en función de la calidad del código, la organización, la atención al detalle y la precisión en la implementación de los requisitos.

## Objetivo general

Desarrollar un sistema web que permita gestionar el acceso de vehículos a un estacionamiento, con una interfaz frontend en Angular y una API backend en Spring Boot.

El sistema debe registrar entradas y salidas, calcular cobros y mostrar reportes.

---

## Parte 1 – Backend (Spring Boot)

### Tecnologías

- Java 17+
- Spring Boot 3.x
- Maven
- Hibernate + JPA
- Base de datos H2 (en memoria)
- Lombok *(opcional)*
- Swagger *(opcional)*

### 1. Endpoints requeridos

| Método | Ruta                          | Descripción                                        |
|--------|-------------------------------|----------------------------------------------------|
| POST   | `neo/vehiculos/oficiales`     | Alta de vehículo oficial                           |
| POST   | `neo/vehiculos/residentes`    | Alta de vehículo residente                         |
| POST   | `neo/vehiculos/no-residentes` | Alta de vehículo no residente *(opcional)*         |
| POST   | `neo/estancias/entrada`       | Registrar entrada de vehículo                      |
| POST   | `neo/estancias/salida`        | Registrar salida de vehículo                       |
| GET    | `neo/residentes/pagos`        | Generar informe de pagos                           |
| POST   | `neo/mes/iniciar`             | Reiniciar mes (reseteo de tiempos y estancias)     |

### 2. Modelos principales

| Modelo    | Atributos                                         |
|-----------|---------------------------------------------------|
| Vehículo  | placa, tipo                                       |
| Estancia  | placa, fecha/hora de entrada, fecha/hora de salida |
| Residente | placa, tiempo acumulado                           |

### 3. Reglas de negocio

| Tipo de vehículo | Tarifa           | Momento del cobro          |
|------------------|------------------|----------------------------|
| Oficial          | No paga          | —                          |
| Residente        | $0.05 / minuto   | Acumulado (informe de pagos) |
| No residente     | $0.50 / minuto   | Al salir                   |

- El sistema debe permitir agregar fácilmente nuevos tipos de vehículos.

### 4. Pruebas unitarias

- Crear pruebas mínimas para los servicios más importantes (p. ej.: cálculo de cobro, registro de estancias).

---

## Parte 2 – Frontend (Angular)

### Tecnologías

- Angular 16+
- Angular Material
- SCSS o CSS

### 1. Requerimientos funcionales

- Página de listado de vehículos (consumida desde el backend).
- Formularios para:
  - Registrar entrada.
  - Registrar salida.
  - Dar de alta vehículo oficial o residente.
  - Iniciar nuevo mes.
- Vista de detalle para cada vehículo (estancias y pagos acumulados, si aplica).
- Página de reporte de residentes (visualización del informe).

### 2. Extras recomendados

- Filtro por placa en el listado.
- Paginación y ordenamiento.
- Validación de campos.
- Prevenir XSS en campos de entrada.
- Pruebas unitarias básicas con Jasmine + Karma.
- Utilizar Angular Material para mejorar el diseño y la experiencia de usuario.

---

## Entrega

Repositorio en GitHub con:

- Proyecto Angular (`frontend/`).
- Proyecto Spring Boot (`backend/`).
- Instrucciones claras en el `README` para:
  - Levantar backend y frontend.
  - Realizar pruebas.
- Base de datos precargada *(opcional)*.
- Capturas de pantalla del frontend funcionando.
- Código bien organizado, modular y con buenas prácticas.

---

## Decisiones técnicas

> Esta sección no forma parte del enunciado original; registra las decisiones tomadas sobre los puntos ambiguos.

### Definidas

| # | Tema | Decisión |
|---|------|----------|
| 1 | Extensibilidad de tipos de vehículo | Tipos como **catálogo administrable** desde la interfaz (nombre, modelo de cobro, tarifa) y patrón **Strategy** por modelo de cobro, sin `if`/`switch` por tipo en los servicios. |
| 5 | Informe de pagos (`GET neo/residentes/pagos`) | Se entrega como **archivo Excel descargable** (`.xlsx`). |
| 6 | Manejo de montos | Uso de **`BigDecimal`** para tarifas y cobros, con **2 decimales** (`setScale(2, RoundingMode.HALF_UP)`). |
| 6b | Minutos parciales | **Todo minuto iniciado se cobra completo** (redondeo hacia arriba): un segundo de exceso ya suma un minuto. Ej.: 10 min 5 s → 11 minutos; 59 s → 1 minuto; 0 s → 0. La interfaz muestra la duración real en formato `mm:ss` junto a los minutos cobrados. |

### 2. Vehículos no registrados

- Una placa sin alta que registra entrada **se acepta y se trata como no residente**; no se rechaza (el alta de no residentes es opcional en el enunciado, por lo que "no residente" es el tipo por defecto).
- En esa primera entrada se crea automáticamente el vehículo con tipo `NO_RESIDENTE`, para que aparezca en el listado y en la vista de detalle.
- Si después se da de alta esa placa como oficial o residente, el vehículo **se reclasifica**. El cambio aplica a las estancias que cierren a partir de ese momento; los cobros ya realizados no se recalculan.
- Dar de alta una placa que ya es oficial o residente responde `409 Conflict`.

### 3. Reinicio de mes (`POST neo/mes/iniciar`)

- Las estancias **no se eliminan, se archivan**: cada estancia cerrada se marca como perteneciente al periodo que termina. Así se conserva el historial y la trazabilidad de lo cobrado.
- El **tiempo acumulado de los residentes vuelve a 0**.
- Las vistas y el informe de pagos muestran solo el **periodo en curso**; el historial queda disponible en la vista de detalle del vehículo.
- Las **estancias abiertas** (vehículos que siguen dentro) no se tocan: su tiempo se contabiliza en el periodo en que registren la salida.
- En el frontend, la acción pide **confirmación** antes de ejecutarse, porque no es reversible.

### 4. Validaciones de entrada y salida

| Caso | Respuesta |
|------|-----------|
| Entrada de una placa que ya tiene una estancia abierta | `409 Conflict` ("el vehículo ya se encuentra dentro") |
| Salida de una placa sin estancia abierta | `409 Conflict` ("el vehículo no tiene una entrada registrada") |
| Placa con formato inválido | `400 Bad Request` |

- **Fecha y hora las asigna el servidor**, no el cliente, para evitar manipulaciones y garantizar que la salida sea siempre posterior a la entrada.
- **Formato de placa (ampliado):** antes de validar se normaliza (mayúsculas, sin espacios ni guiones) y luego debe cumplir `^[A-Z0-9]{3,10}$`. El rango cubre placas particulares, de motocicleta, de servicio público, diplomáticas, personalizadas y extranjeras; solo se admiten letras y dígitos. La misma regla se valida en el frontend (formularios reactivos) y en el backend (Bean Validation), lo que además cierra la puerta a entradas con caracteres para XSS.
- Los errores se devuelven en formato estándar **RFC 7807 (`ProblemDetail`)** mediante un `@RestControllerAdvice`, y el frontend los muestra con un `MatSnackBar`.
- Entrada y salida se ejecutan en una **transacción con bloqueo pesimista sobre el vehículo** (`@Lock(PESSIMISTIC_WRITE)`), para que dos peticiones simultáneas no abran dos estancias para la misma placa.
