# Epic 04: Calidad y entrega

> Al terminar esta épica existen el pipeline de CI, la verificación automática de accesibilidad, el
> informe de cobertura del frontend, el README completo, las 6 capturas y el texto del pull request;
> el repositorio está listo para que la persona lo suba y abra el PR.

| | |
|---|---|
| **Epic id** | `04-calidad-entrega` |
| **Tasks** | `E4-T1` … `E4-T2` |
| **Depends on** | `01-base` (README esqueleto), `02-backend` (completo), `03-frontend` (completo) |
| **Unlocks** | nada — es la última épica |
| **Parallel with** | ninguna |

You do not need any other file to complete this epic. Everything below is repeated here on purpose.

---

## Stack

Java 21 · Spring Boot 4 · H2 · Flyway · Angular 22 · Angular Material · Karma + Jasmine · GitHub
Actions. Shell: **Git Bash**, desde la raíz del proyecto. Maven no está instalado: siempre `./mvnw`.

| Task | Command |
|---|---|
| Backend — puerta | `(cd backend && ./mvnw -q -B -ntp verify)` |
| Backend — arrancar (para capturas) | `(cd backend && ./mvnw spring-boot:run)` — http://localhost:8080 |
| Frontend — pruebas | `(cd frontend && npm run test:ci)` · con cobertura: `(cd frontend && npm run test:cov)` |
| Frontend — formato | `(cd frontend && npm run format:check)` |
| Frontend — build | `(cd frontend && npm run build)` |
| Frontend — arrancar (para capturas) | `(cd frontend && npm start)` — http://localhost:4200 |
| Salud del backend | `curl -sf http://localhost:8080/actuator/health` |

**Gate:** `(cd backend && ./mvnw -q -B -ntp verify)` y `(cd frontend && npm run format:check && npm run test:ci && npm run build)`
pass before any task here is marked done. Cada línea de `Verify` se ejecuta en su propio shell desde
la raíz del proyecto.

No hay servicios locales. Para las capturas se arrancan backend y frontend en segundo plano y se
detienen al terminar.

## Directory subtree

Only the parts this epic touches:

```
./
  .github/workflows/ci.yml          # NEW E4-T1
  README.md                         # exists (esqueleto de 01-base) — E4-T2 lo completa
  docs/
    decisiones.md                   # exists — E4-T2 lo revisa
    PR.md                           # NEW E4-T2
    capturas/                       # NEW E4-T2 — lista.png, detalle.png, operacion-entrada.png,
                                    #   operacion-salida.png, iniciar-mes.png, reporte.png
  frontend/
    package.json                    # exists — E4-T1 añade el script test:cov
    src/app/accesibilidad.spec.ts   # NEW E4-T1
    src/**                          # exists — E4-T1 solo correcciones de accesibilidad
```

Everything outside this subtree is out of scope. If a task seems to require editing a file not
listed here, stop and report — it means the epic boundary is wrong.

## Data model touched here

| Entity | Fields this epic adds or reads | Notes |
|---|---|---|
| — | ninguno | La precarga (6 vehículos: `OFI001`, `OFI002`, `RES001`, `RES002`, `RES003`, `NOR001`; `OFI002` dentro; acumulados 210/10.50, 30/1.50, 60/3.00; total 15.00) se describe en el README |

## Contracts

**Consumed** — already exists, do not rebuild:

| From | Interface | Guarantee |
|---|---|---|
| `01-base` | `README.md` con `## Cómo levantar` y `## Pruebas`; `docs/decisiones.md` | Se completan sin renombrar secciones |
| `02-backend` | `./mvnw -B -ntp verify` en verde; 10 endpoints bajo `/neo`; Swagger en `/swagger-ui.html`; salud en `/actuator/health` | CI y capturas dependen de ello |
| `03-frontend` | Scripts `start`, `test:ci`, `format:check`; cinco páginas standalone con plantilla `.html` externa y un `h1`; `styles.scss` como único archivo con hexadecimales | `accesibilidad.spec.ts` crea las páginas con `provideHttpClientTesting` y `provideRouter([])` |
| Máquina | Google Chrome en `/c/Program Files/Google/Chrome/Application/chrome.exe`; Playwright MCP opcional | Capturas |

**Produced** — later epics depend on exactly these signatures. Changing one breaks them:

| Export | Signature | Used by |
|---|---|---|
| `.github/workflows/ci.yml` | Trabajos `backend` (Java 21, `chmod +x mvnw`, `./mvnw -B -ntp verify`) y `frontend` (Node 24, `npm ci`, `npm run format:check`, `npm run test:ci`, `npm run build`) | La persona, al abrir el PR |
| `docs/PR.md` | Título `Prueba técnica Full Stack – Xochitl (feature/xochitlgr)`, destino `VictorMC96:main`, pasos manuales | La persona |
| `README.md` | Secciones `## Cómo levantar`, `## Pruebas`, `## Base de datos precargada`, `## API y Swagger`, `## Decisiones técnicas`, `## Capturas` | El evaluador |

## Conventions that bite in this area

- Las versiones `@v4` de las acciones de GitHub no se verificaron en la sesión de diseño: confirma
  en la página de cada acción que siguen vigentes antes de darlas por buenas.
- El workflow no se ejecuta durante la construcción (nadie hace push); se valida por contenido.
- **Nunca** `git push`, crear el fork ni abrir el PR: son pasos de la persona y van escritos en
  `docs/PR.md`.
- Toma `iniciar-mes.png` con el diálogo abierto, antes de confirmar: iniciar el mes vacía el informe
  del periodo activo (o reinicia el backend después para recuperar la precarga).
- Detén los procesos de backend y frontend al terminar las capturas.
- El README es para el evaluador: comandos copiables, sin suposiciones sobre herramientas que no pide
  (no hace falta Maven ni Docker).

Full project rules: `CLAUDE.md`. Area rules: `.claude/rules/backend.md`, `.claude/rules/frontend.md`.
All sit in the project root — the builder copied them there from the bundle's `workspace/` before task one.

---

## Tasks

Listed in the same order as `tasks.json`. That order is the build order — work top to bottom and do
not re-rank by priority or by what looks quick.

### `E4-T1` — Añadir CI y verificación de accesibilidad

**Depends on:** `E2-T7`, `E3-T2`, `E3-T3`, `E3-T4`, `E3-T5` · **Priority:** p1 — metadato para recortes de alcance, no un orden de ejecución · **Paso global:** 14

**Objetivo:** Existe el workflow de GitHub Actions con los dos trabajos, una spec que comprueba la estructura accesible de las 5 páginas y el informe de cobertura del frontend.

Dos cosas que cierran la calidad: el pipeline de CI y una comprobación automática de la estructura
accesible de todas las páginas.

**1. `.github/workflows/ci.yml`** — contenido:

```yaml
name: CI

on:
  push:
    branches: [main, feature/**]
  pull_request:

jobs:
  backend:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: backend
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
          cache: maven
      - run: chmod +x mvnw
      - run: ./mvnw -B -ntp verify

  frontend:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: frontend
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '24'
          cache: npm
          cache-dependency-path: frontend/package-lock.json
      - run: npm ci
      - run: npm run format:check
      - run: npm run test:ci
      - run: npm run build
```

Las versiones de las acciones (`@v4`) no se verificaron contra GitHub en la sesión de diseño: antes
de darlas por buenas, confirma en la página de cada acción que esa major sigue vigente y ajústala si
no. El workflow no se ejecuta durante la construcción (nadie hace push); se valida por su contenido.
En los runners de GitHub, Chrome ya está instalado y `karma-chrome-launcher` lo encuentra.

**2. `frontend/package.json`** — añade el script:

```json
"test:cov": "ng test --no-watch --no-progress --browsers=ChromeHeadless --code-coverage"
```

**3. `frontend/src/app/accesibilidad.spec.ts`** — una spec que, para cada una de las 5 páginas
(`VehiculosLista`, `VehiculoDetalle`, `Operacion`, `Reporte`, `NoEncontrado`), crea el componente con
`provideHttpClientTesting` y `provideRouter([])`, responde a sus peticiones con datos de ejemplo,
espera con `await fixture.whenStable()` y afirma sobre el DOM renderizado:

- hay exactamente un `h1`;
- todo `input` tiene etiqueta: `input.labels.length > 0`, o `aria-label`, o `aria-labelledby`;
- todo `button` tiene nombre accesible: texto no vacío, o `aria-label`.

**4. Correcciones** — arregla en `frontend/src/**` lo que la spec y estas búsquedas destapen: ningún
`innerHTML` en `frontend/src/app`; ningún color hexadecimal en los `.scss` de `frontend/src/app` (los
colores viven en `src/styles.scss`); botones de solo icono con `aria-label`; foco visible (no quites
el `outline`). `MatDialog` ya atrapa y devuelve el foco: no lo reimplementes.

La revisión con teclado y a 375px de ancho necesita una persona: está en las puertas manuales de la
entrega, no en este paso.

**Files**

- `.github/workflows/ci.yml` — new: trabajos `backend` y `frontend`
- `frontend/package.json` — edit: script `test:cov`
- `frontend/src/app/accesibilidad.spec.ts` — new: comprueba `h1`, etiquetas y nombres accesibles en las 5 páginas
- `frontend/src/**` — edit: correcciones de accesibilidad que la spec y las búsquedas destapen

**Acceptance**

Copiados literalmente del arreglo `acceptance` de esta tarea en `tasks.json`. Cada uno lo decide un comando de abajo, en esta máquina, durante la construcción.

1. **WHEN** `.github/workflows/ci.yml` is read **THE SYSTEM SHALL** define a job `backend` that runs `./mvnw -B -ntp verify` in `backend/` on Java 21 and a job `frontend` that runs `npm ci`, `npm run format:check`, `npm run test:ci` and `npm run build` in `frontend/` on Node 24.
2. **WHEN** the `backend` job runs on Linux **THE SYSTEM SHALL** mark the wrapper executable with `chmod +x mvnw` before invoking it.
3. **WHEN** `accesibilidad.spec.ts` renders each of the 5 page components **THE SYSTEM SHALL** find exactly one `h1` per page, a label for every `input` and an accessible name for every `button`.
4. **WHEN** the sources under `frontend/src/app` are searched **THE SYSTEM SHALL** find no occurrence of `innerHTML` and no hexadecimal color in any `.scss` file.
5. **WHEN** `npm run test:cov` runs in `frontend/` **THE SYSTEM SHALL** exit 0 and write a coverage report under `frontend/coverage/`.

**Verify** — todos los comandos, en orden, cada uno en su propio shell desde la raíz del proyecto. Cada uno sale con 0 cuando la tarea está bien; la tarea está hecha cuando el último sale con 0.

```bash
test -f .github/workflows/ci.yml   # expect: exit 0
grep -q "^  backend:" .github/workflows/ci.yml && grep -q "^  frontend:" .github/workflows/ci.yml   # expect: exit 0 — los dos trabajos existen
grep -q "mvnw -B -ntp verify" .github/workflows/ci.yml && grep -q "chmod +x mvnw" .github/workflows/ci.yml   # expect: exit 0
grep -q "npm ci" .github/workflows/ci.yml && grep -q "npm run format:check" .github/workflows/ci.yml && grep -q "npm run test:ci" .github/workflows/ci.yml && grep -q "npm run build" .github/workflows/ci.yml   # expect: exit 0
grep -rl "innerHTML" frontend/src/app; test $? -eq 1   # expect: código 1 = sin coincidencias → la línea sale con 0
grep -rlE --include="*.scss" "#[0-9a-fA-F]{3,8}" frontend/src/app; test $? -eq 1   # expect: código 1 = ningún color hexadecimal en estilos de componentes → la línea sale con 0
test -f frontend/src/app/accesibilidad.spec.ts   # expect: exit 0
(cd frontend && npm run format:check)   # expect: exit 0
(cd frontend && npm run test:cov)   # expect: exit 0, 0 fallos, 0 omitidos
test -d frontend/coverage   # expect: exit 0 — directorio por defecto del informe de cobertura
(cd frontend && npm run build)   # expect: exit 0
(cd backend && ./mvnw -q -B -ntp verify)   # expect: exit 0 — la puerta de backend sigue en verde
```

Son el arreglo `verify` de esta tarea en `tasks.json`, una línea por comando, con la afirmación de código de salida intacta (`...; test $? -eq 1`). No los edites ni los "simplifiques".

**Checkpoint**

```bash
git add -A && git commit -q -m "ci: workflow de backend y frontend, pruebas de accesibilidad y cobertura [E4-T1]" -m "Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
git tag step-14-ci-accesibilidad
git ls-files --error-unmatch .github/workflows/ci.yml   # expect: exit 0
git check-ignore -q frontend/coverage/index.html   # expect: exit 0 — el informe de cobertura no se versiona
# punto de retorno si el paso siguiente sale mal: git reset --hard step-14-ci-accesibilidad
```

Ejecuta el bloque después de que el último comando de Verify salga con 0 y antes de empezar la tarea siguiente. La etiqueta es el valor `checkpoint` de esta tarea en `tasks.json`; si esta tarea sale mal, vuelve con `git reset --hard step-13-reporte-residentes`.

### `E4-T2` — Completar README, capturas y texto del PR

**Depends on:** `E1-T1`, `E4-T1` · **Priority:** p0 — metadato para recortes de alcance, no un orden de ejecución · **Paso global:** 15

**Objetivo:** El README en español explica cómo levantar y probar todo, las 6 capturas están en `docs/capturas/` y `docs/PR.md` contiene el texto del PR y los pasos manuales de entrega.

Último paso: lo que el evaluador lee y ve primero. No se escribe código de producto.

**1. `README.md`** — versión completa en español, con estas secciones (los títulos son literales):

- `# Gestión de Acceso Vehicular` — qué es, en dos o tres líneas, y el stack con las versiones reales
  leídas de `backend/pom.xml` y `frontend/package.json`.
- `## Requisitos` — Java 21, Node 24 y npm; no hace falta Maven (se usa el wrapper) ni Docker.
- `## Cómo levantar` — backend: `cd backend` y `./mvnw spring-boot:run` (API en
  `http://localhost:8080`); frontend: `cd frontend`, `npm ci` y `npm start` (interfaz en
  `http://localhost:4200`, con proxy de `/neo` al backend). En Windows, indicar `mvnw.cmd` como
  alternativa fuera de Git Bash.
- `## Pruebas` — `cd backend && ./mvnw verify` (pruebas, formato Spotless y cobertura JaCoCo ≥ 80 % en
  dominio y aplicación; informe en `backend/target/site/jacoco/index.html`); `cd frontend && npm run
  test:ci` y `npm run format:check`; nota sobre `CHROME_BIN` si Karma no encuentra Chrome.
- `## Base de datos precargada` — H2 en memoria migrada con Flyway; tabla con los 6 vehículos de la
  precarga (`OFI001`, `OFI002`, `RES001`, `RES002`, `RES003`, `NOR001`), cuál está dentro (`OFI002`) y
  los acumulados de residentes (210 / 10.50, 30 / 1.50, 60 / 3.00; total 15.00).
- `## API y Swagger` — tabla de los 10 endpoints bajo `/neo` y los enlaces
  `http://localhost:8080/swagger-ui.html` y `http://localhost:8080/v3/api-docs`; ejemplo de error
  `ProblemDetail` con `code`.
- `## Decisiones técnicas` — resumen de cinco o seis líneas y enlace a `docs/decisiones.md` y a
  `Requerimientos.md`. Incluye de forma explícita la nota sobre Spring Boot 4 frente al "3.x" del
  enunciado.
- `## Capturas` — las 6 imágenes, cada una con su ruta `docs/capturas/<nombre>.png` y un pie de foto.
- `## Metodología` — dos líneas: blueprint en `blueprints/gestion-acceso-vehicular/` y Spec Kit en `.specify/`.

**2. Capturas** en `docs/capturas/` — exactamente estos 6 archivos PNG: `lista.png` (listado con la
precarga), `detalle.png` (`/vehiculos/RES001`), `operacion-entrada.png` (entrada registrada),
`operacion-salida.png` (salida con minutos e importe), `iniciar-mes.png` (diálogo de confirmación
abierto), `reporte.png` (informe con total). Procedimiento: arranca el backend (`cd backend &&
./mvnw spring-boot:run`) y el frontend (`cd frontend && npm start`) en segundo plano; espera a que
`curl -s http://localhost:8080/actuator/health` responda y a que `http://localhost:4200` cargue; toma
las capturas a 1440×900 con las herramientas de navegador de Playwright MCP si están disponibles
(navegar, rellenar el formulario, capturar). Si no lo están, usa Chrome sin interfaz, que basta para
las vistas sin interacción y deja igualmente los 6 archivos:

```bash
"/c/Program Files/Google/Chrome/Application/chrome.exe" --headless --window-size=1440,900 --screenshot="docs/capturas/lista.png" http://localhost:4200/vehiculos
```

Toma `iniciar-mes.png` **antes** de confirmar el diálogo, o reinicia el backend después: iniciar el
mes vacía el informe del periodo activo. Al terminar, detén ambos procesos.

**3. `docs/PR.md`** — contiene:

- Título del PR: `Prueba técnica Full Stack – Xochitl (feature/xochitlgr)`.
- Cuerpo: resumen de lo entregado, cómo probarlo (los comandos del README), decisiones destacadas y
  lista de capturas.
- Pasos manuales de entrega, **que ejecuta la persona y nunca el agente**: (1) crear el fork de
  `VictorMC96/Full-Stack-Prueba-tecnica-Neology` en GitHub; (2) `git remote add origin <URL del fork>`;
  (3) `git push -u origin feature/xochitlgr`; (4) abrir el pull request hacia `VictorMC96:main`;
  (5) enviar el correo a la consultora con copia a `vmiranda@neology.mx` y `lluna@neopartners.mx`.
- Si se usaron ramas por épica, recordatorio de integrarlas antes con `git merge --no-ff <rama>` sobre
  `feature/xochitlgr`.

**4. `docs/decisiones.md`** — revisa que refleje lo que realmente se construyó; corrige cualquier
decisión que haya cambiado.

El agente **no** hace push, no crea el fork y no abre el PR.

**Files**

- `README.md` — edit: versión completa en español
- `docs/capturas/*.png` — new: 6 capturas del frontend funcionando
- `docs/PR.md` — new: título, cuerpo y pasos manuales del pull request
- `docs/decisiones.md` — edit: ajustes finales si algo cambió durante la construcción

**Acceptance**

Copiados literalmente del arreglo `acceptance` de esta tarea en `tasks.json`. Cada uno lo decide un comando de abajo, en esta máquina, durante la construcción.

1. **WHEN** `README.md` is read **THE SYSTEM SHALL** contain the headings `## Cómo levantar`, `## Pruebas`, `## Base de datos precargada`, `## API y Swagger`, `## Decisiones técnicas` and `## Capturas`.
2. **WHEN** the commands in the README are inspected **THE SYSTEM SHALL** state `./mvnw spring-boot:run` for `backend/`, `npm ci` followed by `npm start` for `frontend/`, and the URLs `http://localhost:8080/swagger-ui.html` and `http://localhost:4200`.
3. **WHEN** `docs/capturas/` is listed **THE SYSTEM SHALL** contain the 6 files `lista.png`, `detalle.png`, `operacion-entrada.png`, `operacion-salida.png`, `iniciar-mes.png` and `reporte.png`, each referenced from `README.md`.
4. **WHEN** `docs/PR.md` is read **THE SYSTEM SHALL** contain the pull-request title `Prueba técnica Full Stack – Xochitl (feature/xochitlgr)`, the target `VictorMC96:main` and the manual fork, push and pull-request steps, none of which the builder executes.
5. **WHEN** the full gates run **THE SYSTEM SHALL** exit 0 on `./mvnw -q -B -ntp verify` in `backend/` and on `npm run format:check`, `npm run test:ci` and `npm run build` in `frontend/`.

**Verify** — todos los comandos, en orden, cada uno en su propio shell desde la raíz del proyecto. Cada uno sale con 0 cuando la tarea está bien; la tarea está hecha cuando el último sale con 0.

```bash
grep -q "## Cómo levantar" README.md && grep -q "## Pruebas" README.md && grep -q "## Base de datos precargada" README.md   # expect: exit 0
grep -q "## API y Swagger" README.md && grep -q "## Decisiones técnicas" README.md && grep -q "## Capturas" README.md   # expect: exit 0
grep -q "mvnw spring-boot:run" README.md && grep -q "npm ci" README.md && grep -q "npm start" README.md   # expect: exit 0
grep -q "http://localhost:8080/swagger-ui.html" README.md && grep -q "http://localhost:4200" README.md   # expect: exit 0
test -f docs/capturas/lista.png && grep -q "docs/capturas/lista.png" README.md   # expect: exit 0
test -f docs/capturas/detalle.png && grep -q "docs/capturas/detalle.png" README.md   # expect: exit 0
test -f docs/capturas/operacion-entrada.png && grep -q "docs/capturas/operacion-entrada.png" README.md   # expect: exit 0
test -f docs/capturas/operacion-salida.png && grep -q "docs/capturas/operacion-salida.png" README.md   # expect: exit 0
test -f docs/capturas/iniciar-mes.png && grep -q "docs/capturas/iniciar-mes.png" README.md   # expect: exit 0
test -f docs/capturas/reporte.png && grep -q "docs/capturas/reporte.png" README.md   # expect: exit 0
grep -q "feature/xochitlgr" docs/PR.md && grep -q "VictorMC96:main" docs/PR.md   # expect: exit 0
(cd backend && ./mvnw -q -B -ntp verify)   # expect: exit 0
(cd frontend && npm run format:check)   # expect: exit 0
(cd frontend && npm run test:ci)   # expect: exit 0, 0 fallos, 0 omitidos
(cd frontend && npm run build)   # expect: exit 0
```

Son el arreglo `verify` de esta tarea en `tasks.json`, una línea por comando, con la afirmación de código de salida intacta (`...; test $? -eq 1`). No los edites ni los "simplifiques".

**Checkpoint**

```bash
git add -A && git commit -q -m "docs: README completo, capturas y guia del pull request [E4-T2]" -m "Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
git tag step-15-readme-entrega
git ls-files --error-unmatch docs/PR.md   # expect: exit 0
git ls-files --error-unmatch docs/capturas/lista.png   # expect: exit 0 — *.png es binario en .gitattributes y no está ignorado
# punto de retorno si el paso siguiente sale mal: git reset --hard step-15-readme-entrega
```

Ejecuta el bloque después de que el último comando de Verify salga con 0 y antes de empezar la tarea siguiente. La etiqueta es el valor `checkpoint` de esta tarea en `tasks.json`; si esta tarea sale mal, vuelve con `git reset --hard step-14-ci-accesibilidad`.

---

## Epic acceptance

The epic is done when every task is `done` **and**:

1. **WHEN** the full gates run from a clean checkout **THE SYSTEM SHALL** exit 0 on `./mvnw -q -B -ntp verify` in `backend/` and on `npm run format:check`, `npm run test:ci` and `npm run build` in `frontend/`.
2. **WHEN** the delivery files are listed **THE SYSTEM SHALL** find `.github/workflows/ci.yml`, `docs/PR.md`, the 6 PNG files under `docs/capturas/` and a `README.md` containing `## Cómo levantar`.

```bash
(cd backend && ./mvnw -q -B -ntp verify)
(cd frontend && npm run format:check && npm run test:ci && npm run build)
test -f .github/workflows/ci.yml && test -f docs/PR.md && grep -q "## Cómo levantar" README.md && test -f docs/capturas/lista.png && test -f docs/capturas/detalle.png && test -f docs/capturas/operacion-entrada.png && test -f docs/capturas/operacion-salida.png && test -f docs/capturas/iniciar-mes.png && test -f docs/capturas/reporte.png
```

Run from the project root. Both criteria must be decidable by these commands.

## Pitfalls

- **Capturas con el backend caído** — el listado saldría vacío con un snackbar de error; comprueba
  `/actuator/health` antes de capturar.
- **Olvidar `chmod +x mvnw` en CI** — en Windows el bit de ejecución no se registra siempre; el
  trabajo `backend` fallaría con `Permission denied`.
- **`accesibilidad.spec.ts` sin responder las peticiones HTTP** — las páginas quedarían en estado de
  carga y algunos controles no se renderizan; responde con datos de ejemplo antes de `whenStable()`.
- **Empujar la rama "para probar CI"** — está prohibido; el workflow se valida por contenido.

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
- [ ] Los procesos de backend y frontend usados para las capturas están detenidos.
