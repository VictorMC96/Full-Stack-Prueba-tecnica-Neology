# Epic 03: Frontend

> Al terminar esta épica la interfaz Angular tiene listado paginado, detalle, formularios de operación
> y reporte de residentes, con Material 3, validación de placa, estados de carga/vacío/error y pruebas
> Karma en verde.

| | |
|---|---|
| **Epic id** | `03-frontend` |
| **Tasks** | `E3-T1` … `E3-T5` |
| **Depends on** | nothing — start here (solo requiere el bloque Bootstrap de `blueprint.md` §10). Para probar a mano contra datos reales hace falta el backend de `02-backend`; las pruebas automáticas no lo necesitan |
| **Unlocks** | `04-calidad-entrega` |
| **Parallel with** | `01-base`, `02-backend` — no comparten archivos. Dentro de la épica, `E3-T2` a `E3-T5` solo dependen de `E3-T1` y tocan carpetas distintas |

You do not need any other file to complete this epic. Everything below is repeated here on purpose.

---

## Stack

Angular 22 standalone y zoneless · TypeScript · Angular Material 3 + CDK · SCSS · Reactive Forms ·
Karma + Jasmine en ChromeHeadless · Prettier. Shell: **Git Bash**, desde la raíz del proyecto.
`frontend/package.json` y `frontend/angular.json` los generó `ng new`: solo se editan. Las versiones
que instala `E3-T1` son las de la tabla de dependencias del blueprint (2026-09-30):
`@angular/material@22.2.1`, `@angular/cdk@22.2.1`, `prettier@3.9.9`.

| Task | Command |
|---|---|
| Instalar | `(cd frontend && npm ci)` |
| Arrancar | `(cd frontend && npm start)` — http://localhost:4200 (proxy de `/neo` y `/actuator` a http://localhost:8080) |
| Pruebas | `(cd frontend && npm run test:ci)` |
| Formato | `(cd frontend && npm run format:check)` · corregir: `(cd frontend && npm run format)` |
| Build | `(cd frontend && npm run build)` |
| Backend para pruebas manuales | `(cd backend && ./mvnw spring-boot:run)` |

**Gate:** `(cd frontend && npm run format:check)`, `(cd frontend && npm run test:ci)` y
`(cd frontend && npm run build)` pass before any task here is marked done. Cada línea de `Verify`
se ejecuta en su propio shell desde la raíz del proyecto.

Si Karma no encuentra Chrome: `export CHROME_BIN="/c/Program Files/Google/Chrome/Application/chrome.exe"`
en el mismo shell y repite. No hay otros servicios locales.

## Directory subtree

Only the parts this epic touches:

```
frontend/
  package.json                 # exists (ng new) — E3-T1 añade dependencias y scripts
  angular.json                 # exists — E3-T1 añade proxyConfig al target serve
  proxy.conf.json              # NEW E3-T1
  .prettierrc, .prettierignore # NEW E3-T1
  src/
    index.html                 # exists — E3-T1: lang="es", título, fuentes
    styles.scss                # exists — E3-T1: mat.theme + tokens
    styles/                    # NEW E3-T1 — parcial generado por el schematic theme-color
    app/
      app.ts, app.html, app.scss, app.spec.ts, app.config.ts, app.routes.ts   # exist — E3-T1 los edita
      app.routes.spec.ts       # NEW E3-T1
      layout/shell/            # NEW E3-T1 — shell.ts/.html/.scss/.spec.ts
      core/api/                # NEW E3-T1 — vehiculos-api.ts, estancias-api.ts, mes-api.ts, informe-api.ts (+ specs)
      core/modelos/modelos.ts  # NEW E3-T1
      core/interceptores/problem-detail.interceptor.ts   # NEW E3-T1 (+ spec)
      shared/placa/            # NEW E3-T1 — placa.validator.ts, placa-mayusculas.directive.ts (+ specs)
      shared/estado-chip/      # NEW E3-T1
      shared/empty-state/      # NEW E3-T1
      shared/loading/          # NEW E3-T1
      features/vehiculos/lista/    # E3-T1 relleno → E3-T2 real (vehiculos-lista.ts/.html/.scss/.spec.ts)
      features/vehiculos/detalle/  # E3-T1 relleno → E3-T4 real (vehiculo-detalle.*)
      features/operacion/          # E3-T1 relleno → E3-T3 real (operacion.*, entrada-form, salida-form, alta-form, iniciar-mes, confirmar-dialog)
      features/reporte/            # E3-T1 relleno → E3-T5 real (reporte.*)
      features/no-encontrado/      # E3-T1 definitivo
```

Everything outside this subtree is out of scope. If a task seems to require editing a file not
listed here, stop and report — it means the epic boundary is wrong.

## Data model touched here

| Entity | Fields this epic adds or reads | Notes |
|---|---|---|
| — | ninguno directamente | El frontend solo consume la API; las formas están en "Contracts" |

## Contracts

**Consumed** — already exists (lo produce `02-backend`), do not rebuild:

| From | Interface | Guarantee |
|---|---|---|
| `02-backend` | `POST /neo/vehiculos/oficiales` · `/residentes` · `/no-residentes` con `{ "placa": string }` | 201 + `VehiculoResponse`; 400 `VALIDACION`; 409 `VEHICULO_YA_REGISTRADO` |
| `02-backend` | `POST /neo/estancias/entrada` · `POST /neo/estancias/salida` con `{ placa }` | 201 / 200 `EstanciaResponse`; 409 `VEHICULO_YA_DENTRO` / `VEHICULO_NO_DENTRO`; 404 `VEHICULO_NO_ENCONTRADO` |
| `02-backend` | `GET /neo/residentes/pagos` | `Accept: application/json` → `InformePagosResponse`; `Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` → `.xlsx` con `Content-Disposition: attachment; filename="informe-pagos-<yyyy-MM>.xlsx"` |
| `02-backend` | `POST /neo/mes/iniciar` | 200 `IniciarMesResponse` |
| `02-backend` | `GET /neo/vehiculos?placa=&page=0&size=10&sort=placa,asc` | `PageResponse<VehiculoResumen>`; `sort` admite `placa`, `tipo`, `createdAt` con `asc`/`desc`; `size` máximo 100 |
| `02-backend` | `GET /neo/vehiculos/{placa}` · `GET /neo/vehiculos/{placa}/estancias?page&size` | `VehiculoDetalleResponse` (404 si no existe) · `PageResponse<EstanciaResponse>` por `entrada` desc |
| `02-backend` | Errores | `application/problem+json` con `status`, `detail`, `code` y, en validación, `errors[{ field, message }]` |

Formas (JSON) que `core/modelos/modelos.ts` debe reproducir exactamente:

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
ProblemDetail           { type, title, status, detail, instance, code, errors?: [{ field, message }] }
TipoVehiculo = "OFICIAL" | "RESIDENTE" | "NO_RESIDENTE" · MomentoCobro = "NINGUNO" | "INFORME_MENSUAL" | "AL_SALIR"
```

Regla de placa, idéntica a la del backend: mayúsculas, sin espacios ni guiones, `^[A-Z0-9]{3,10}$`.

**Produced** — later epics depend on exactly these signatures. Changing one breaks them:

| Export | Signature | Used by |
|---|---|---|
| Páginas `VehiculosLista`, `VehiculoDetalle`, `Operacion`, `Reporte`, `NoEncontrado` | Componentes standalone con plantilla `.html` externa y un único `h1`, creables en `TestBed` con `provideHttpClientTesting` y `provideRouter([])` | `04-calidad-entrega` (`accesibilidad.spec.ts`) |
| Scripts `start`, `test:ci`, `format`, `format:check` en `frontend/package.json` | `ng serve --proxy-config proxy.conf.json` · `ng test --no-watch --no-progress --browsers=ChromeHeadless` · `prettier --write/--check "src/**/*.{ts,html,scss}"` | `04-calidad-entrega` (CI y `test:cov`) |
| `frontend/src/styles.scss` | Único archivo con colores hexadecimales; tokens `--app-*` | `04-calidad-entrega` (búsqueda de hex en `.scss` de componentes) |

## Conventions that bite in this area

- Zoneless: `await fixture.whenStable()` siempre; `fakeAsync`/`tick` no existen. Temporizadores con
  `jasmine.clock().install()`, `mockDate()`, `tick(ms)`, `uninstall()`.
- Control flow `@if`/`@for` con `track`; signals para el estado; Reactive Forms (nunca Signal Forms).
- Solo `core/api/*` inyecta `HttpClient`; rutas relativas `/neo/...`.
- Paginación, orden y filtro en el servidor: no uses el orden ni el filtro de `MatTableDataSource`.
- Todo `input` en `mat-form-field` con `mat-label`; errores en `mat-error`; botones de solo icono con
  `aria-label`; un solo `h1` por página; sin `innerHTML`.
- Colores solo por `var(--app-...)` o `var(--mat-sys-...)`; ningún hexadecimal fuera de `styles.scss`.
- Harnesses de Material (`TestbedHarnessEnvironment`) en lugar de selectores de clases internas.
- `npm run format` antes de cada `verify`. No subas los `budgets` de `angular.json`.
- No instales `@angular/animations` ni zone.js.

Full project rules: `CLAUDE.md`. Area rules: `.claude/rules/frontend.md`. Both sit in the project
root — the builder copied them there from the bundle's `workspace/` before task one.

---

## Tasks

Listed in the same order as `tasks.json`. That order is the build order — work top to bottom and do
not re-rank by priority or by what looks quick.

### `E3-T1` — Montar base del frontend con Material y rutas

**Depends on:** nada — solo el Bootstrap · **Priority:** p0 — metadato para recortes de alcance, no un orden de ejecución · **Paso global:** 9

**Objetivo:** El frontend tiene Angular Material con el tema del proyecto, Prettier, proxy al backend, el shell con navegación, el manifiesto de rutas con páginas de relleno y la capa `core` + `shared` probada.

Convierte el proyecto que generó `ng new` en la base del frontend. `frontend/package.json` y
`frontend/angular.json` son de autoría del CLI; aquí solo se editan. Todo se ejecuta dentro de
`frontend/`.

**1. Dependencias y tema**

```bash
cd frontend
npm i -S @angular/material@22.2.1 @angular/cdk@22.2.1
npm i -D prettier@3.9.9
npx ng generate @angular/material:theme-color --primaryColor="#0F4C5C" --tertiaryColor="#E36414" --directory=src/styles --interactive=false
```

Antes de lanzar el schematic, comprueba los nombres de las opciones con `npx ng generate
@angular/material:theme-color --help` y usa los que liste. El schematic escribe en `src/styles/` un
parcial SCSS que exporta `$primary-palette` y `$tertiary-palette`; el `@use` de `styles.scss` debe
apuntar al archivo que realmente generó. No instales `@angular/animations`.

**2. Scripts en `package.json`** (añade o sustituye estas claves en `"scripts"`):

```json
"start": "ng serve --proxy-config proxy.conf.json",
"test:ci": "ng test --no-watch --no-progress --browsers=ChromeHeadless",
"format": "prettier --write \"src/**/*.{ts,html,scss}\"",
"format:check": "prettier --check \"src/**/*.{ts,html,scss}\""
```

**3. Archivos de configuración nuevos**

`frontend/proxy.conf.json`:

```json
{
  "/neo": { "target": "http://localhost:8080", "secure": false },
  "/actuator": { "target": "http://localhost:8080", "secure": false }
}
```

En `angular.json`, dentro de `projects.frontend.architect.serve.options`, añade `"proxyConfig":
"proxy.conf.json"`. No toques los `budgets`.

`frontend/.prettierrc`:

```json
{
  "singleQuote": true,
  "printWidth": 100,
  "trailingComma": "all",
  "overrides": [{ "files": "*.html", "options": { "parser": "angular" } }]
}
```

`frontend/.prettierignore`:

```
dist
coverage
.angular
node_modules
```

**4. `src/index.html`** — `lang="es"`, `<title>Gestión de Acceso Vehicular</title>` y, en el `<head>`,
`preconnect` a `https://fonts.googleapis.com` y `https://fonts.gstatic.com` más las hojas de Google
Fonts para Inter (400, 500, 600), Roboto Mono (500) y Material Symbols Outlined, con `display=swap`.

**5. `src/styles.scss`**:

```scss
@use '@angular/material' as mat;
@use './styles/theme-colors' as paleta;

html {
  color-scheme: light;
  @include mat.theme(
    (
      color: (
        primary: paleta.$primary-palette,
        tertiary: paleta.$tertiary-palette,
        theme-type: light,
      ),
      typography: (
        plain-family: Inter,
        brand-family: Inter,
      ),
      density: 0,
    )
  );
}

:root {
  --app-color-primary: #0f4c5c;
  --app-color-primary-fg: #ffffff;
  --app-color-accent: #e36414;
  --app-color-background: #f8fafb;
  --app-color-surface: #ffffff;
  --app-color-border: #74777b;
  --app-color-divider: #e0e3e5;
  --app-color-fg: #1a1c1e;
  --app-color-fg-muted: #5c6670;
  --app-color-destructive: #b3261e;
  --app-color-success: #2e7d32;
  --app-space-1: 4px;
  --app-space-2: 8px;
  --app-space-3: 12px;
  --app-space-4: 16px;
  --app-space-5: 24px;
  --app-space-6: 32px;
  --app-space-7: 48px;
  --app-radius-card: 8px;
  --app-radius-chip: 4px;
}

html,
body {
  height: 100%;
}

body {
  margin: 0;
  font-family: Inter, system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif;
  background: var(--app-color-background);
  color: var(--app-color-fg);
}

.placa {
  font-family: 'Roboto Mono', ui-monospace, Consolas, monospace;
  font-weight: 500;
  letter-spacing: 0.08em;
}

@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}
```

Este archivo es el único lugar con colores hexadecimales. Los componentes usan `var(--app-...)` o los
roles `var(--mat-sys-...)`.

**6. Aplicación**

- `app.config.ts` — `provideRouter(routes)`, `provideHttpClient(withInterceptors([problemDetailInterceptor]))`.
- `app.routes.ts` — el manifiesto: `''` redirige a `vehiculos` (`pathMatch: 'full'`); `vehiculos`,
  `vehiculos/:placa`, `operacion`, `reporte` y `**`, cada una con `loadComponent`.
- `app.ts` / `app.html` — solo renderizan `<app-shell />`.
- `layout/shell/shell.ts` (+ `.html`, `.scss`) — `mat-sidenav-container` con `mat-toolbar` (título
  "Gestión de Acceso Vehicular") y `mat-nav-list` con los enlaces Vehículos, Operación y Reporte
  (`routerLink` + `routerLinkActive`), landmarks `<nav>` y `<main>`, y un enlace "Saltar al contenido".
  Bajo 600px el sidenav pasa a modo `over`.
- `features/vehiculos/lista/vehiculos-lista.ts`, `features/vehiculos/detalle/vehiculo-detalle.ts`,
  `features/operacion/operacion.ts`, `features/reporte/reporte.ts`, `features/no-encontrado/no-encontrado.ts`
  — páginas de relleno, **cada una con su archivo `.html` externo** que contiene un único `<h1>`. Los
  pasos 10 a 13 sustituyen su contenido; `no-encontrado` queda definitiva aquí ("Página no encontrada"
  + enlace a `/vehiculos`).

**7. `core`**

- `core/modelos/modelos.ts` — tipos `TipoVehiculo`, `MomentoCobro`, `VehiculoResponse`,
  `VehiculoResumen`, `EstanciaResponse`, `AcumuladoResponse`, `VehiculoDetalleResponse`,
  `PeriodoResponse`, `InformePagosResponse`, `IniciarMesResponse`, `PageResponse<T>`, `ProblemDetail`
  (con `code` y `errors` opcionales) — exactamente las formas del contrato de la API.
- `core/api/vehiculos-api.ts` (`listar`, `detalle`, `estancias`, `alta(tipo, placa)`),
  `core/api/estancias-api.ts` (`entrada`, `salida`), `core/api/mes-api.ts` (`iniciar`),
  `core/api/informe-api.ts` (`obtener`, `descargarExcel` con `responseType: 'blob'` y `observe:
  'response'`). Rutas relativas (`/neo/...`); son los únicos archivos que inyectan `HttpClient`.
- `core/interceptores/problem-detail.interceptor.ts` — interceptor funcional: en error abre un
  `MatSnackBar` con `error.error.detail` (o "No se pudo conectar con el servidor" si no hay
  `ProblemDetail`) y relanza con `throwError`.

**8. `shared`**

- `shared/placa/placa.validator.ts` — `normalizarPlaca(valor)` y `placaValidator`: normaliza
  (mayúsculas, sin espacios ni guiones) y exige `^[A-Z0-9]{3,10}$`; error `{ placa: true }`.
- `shared/placa/placa-mayusculas.directive.ts` — directiva `appPlacaMayusculas` que pasa el valor del
  control a mayúsculas al escribir.
- `shared/estado-chip/estado-chip.ts` — entrada `estado` (`OFICIAL`, `RESIDENTE`, `NO_RESIDENTE`,
  `DENTRO`, `FUERA`); muestra el texto en español y aplica los roles de color de Material 3.
- `shared/empty-state/empty-state.ts` (icono, título, proyección de contenido para la acción) y
  `shared/loading/loading.ts` (`mat-progress-bar` indeterminada con `aria-busy` y `aria-label`).

**9. Pruebas** (una spec junto a cada unidad): `app.spec.ts` (renderiza el shell),
`app.routes.spec.ts` (con `RouterTestingHarness`: `/` termina en `/vehiculos`; cada ruta del
manifiesto carga un componente), `placa.validator.spec.ts`, `problem-detail.interceptor.spec.ts`,
`vehiculos-api.spec.ts`, `estado-chip.spec.ts`. Usa `provideHttpClient()` +
`provideHttpClientTesting()` y, como la app es zoneless, espera con `await fixture.whenStable()`.

**10. Formato** — ejecuta `npm run format` antes de verificar.

Si Karma no encuentra Chrome: `export CHROME_BIN="/c/Program Files/Google/Chrome/Application/chrome.exe"`
y repite.

**Files**

- `frontend/package.json` — edit: dependencias de Material, CDK y Prettier; scripts `start`, `test:ci`, `format`, `format:check`
- `frontend/{angular.json,proxy.conf.json,.prettierrc,.prettierignore}` — edit: `proxyConfig` en `serve`; new: los otros tres
- `frontend/src/{index.html,styles.scss,styles/**}` — edit: fuentes, tema `mat.theme` y tokens; new: paleta generada por el schematic `theme-color`
- `frontend/src/app/{app.ts,app.html,app.scss,app.spec.ts,app.config.ts,app.routes.ts,app.routes.spec.ts,layout/**,features/**}` — edit: raíz y configuración; new: shell, rutas y páginas de relleno
- `frontend/src/app/{core,shared}/**` — new: modelos, servicios de API, interceptor, validador y directiva de placa, `EstadoChip`, `EmptyState`, `Loading`, con sus specs

**Acceptance**

Copiados literalmente del arreglo `acceptance` de esta tarea en `tasks.json`. Cada uno lo decide un comando de abajo, en esta máquina, durante la construcción.

1. **WHEN** `npm run build` runs in `frontend/` **THE SYSTEM SHALL** exit 0 with `@angular/material` and `@angular/cdk` listed in `package.json` and the theme compiled from `src/styles.scss` through `mat.theme`.
2. **WHEN** the router navigates to `/` **THE SYSTEM SHALL** redirect to `/vehiculos`, and the paths `/vehiculos`, `/vehiculos/:placa`, `/operacion`, `/reporte` and `**` SHALL each resolve to a lazily loaded standalone component.
3. **WHEN** `placaValidator` validates `abc-123 d`, `AB` and `ABC<1>` **THE SYSTEM SHALL** accept the first and reject the other two with the error key `placa`.
4. **WHEN** an HTTP call fails with a `ProblemDetail` body **THE SYSTEM SHALL** open a `MatSnackBar` showing its `detail` and rethrow the error to the caller.
5. **WHEN** `VehiculosApi.listar` is called with page 1, size 25, sort `tipo,desc` and filter `res` **THE SYSTEM SHALL** issue a GET to `/neo/vehiculos` with the query parameters `page=1`, `size=25`, `sort=tipo,desc` and `placa=res`.
6. **WHEN** `npm run format:check` and `npm run test:ci` run in `frontend/` **THE SYSTEM SHALL** exit 0 on both.

**Verify** — todos los comandos, en orden, cada uno en su propio shell desde la raíz del proyecto. Cada uno sale con 0 cuando la tarea está bien; la tarea está hecha cuando el último sale con 0.

```bash
(cd frontend && npm run format:check)   # expect: exit 0 — Prettier no encuentra archivos sin formatear
(cd frontend && npm run test:ci)   # expect: exit 0, 0 fallos, 0 omitidos — Karma en ChromeHeadless
(cd frontend && npm run build)   # expect: exit 0
grep -q '"/neo"' frontend/proxy.conf.json && grep -q "proxy.conf.json" frontend/angular.json   # expect: exit 0 — el proxy existe y está cableado en angular.json
grep -q "mat.theme" frontend/src/styles.scss && grep -q "prefers-reduced-motion" frontend/src/styles.scss   # expect: exit 0
grep -q '"test:ci"' frontend/package.json && grep -q '"format:check"' frontend/package.json   # expect: exit 0
```

Son el arreglo `verify` de esta tarea en `tasks.json`, una línea por comando, con la afirmación de código de salida intacta (`...; test $? -eq 1`). No los edites ni los "simplifiques".

**Checkpoint**

```bash
git add -A && git commit -q -m "feat(frontend): base con Material 3, shell, rutas, API y formato [E3-T1]" -m "Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
git tag step-09-frontend-base
git ls-files --error-unmatch frontend/proxy.conf.json   # expect: exit 0
git ls-files --error-unmatch frontend/.prettierrc   # expect: exit 0
# punto de retorno si el paso siguiente sale mal: git reset --hard step-09-frontend-base
```

Ejecuta el bloque después de que el último comando de Verify salga con 0 y antes de empezar la tarea siguiente. La etiqueta es el valor `checkpoint` de esta tarea en `tasks.json`; si esta tarea sale mal, vuelve con `git reset --hard step-08-consultas-calidad`.

### `E3-T2` — Construir listado de vehículos paginado

**Depends on:** `E3-T1` · **Priority:** p0 — metadato para recortes de alcance, no un orden de ejecución · **Paso global:** 10

**Objetivo:** La página `/vehiculos` muestra el listado con paginación, orden y filtro por placa resueltos en el servidor, con estados de carga, vacío y error.

Sustituye la página de relleno de `/vehiculos` por el listado real. Toda la paginación, el orden y el
filtro viajan al servidor: `mat-table` recibe la página ya ordenada y **no** se usa
`MatTableDataSource` con orden ni filtro en cliente.

- `vehiculos-lista.ts` — signals `pagina` (0), `tamano` (10), `orden` (`'placa,asc'`), `filtro` (`''`),
  `datos` (`PageResponse<VehiculoResumen> | null`), `cargando`, `error`. Un método `cargar()` llama a
  `VehiculosApi.listar` con esos valores y escribe el resultado en los signals.
- Filtro: `FormControl<string>` con `appPlacaMayusculas`, dentro de un `mat-form-field` con
  `<mat-label>Filtrar por placa</mat-label>`; `valueChanges` pasa por `debounceTime(300)` y
  `distinctUntilChanged()`, fija `pagina` en 0 y recarga.
- Tabla: `mat-table` con `matSort` (`matSortActive="placa"`, `matSortDirection="asc"`, evento
  `matSortChange` → actualiza `orden` y recarga). Columnas: Placa (`<span class="placa">`), Tipo
  (`EstadoChip`), Estado (`EstadoChip` con `DENTRO` o `FUERA`), Alta (`createdAt` con `DatePipe`), y un
  botón de icono cuyo `aria-label` es "Ver detalle de" seguido de la placa, y que navega al detalle de esa placa.
  Columnas ordenables: placa, tipo y alta (`createdAt`).
- `mat-paginator` con `[length]="datos()?.totalElements"`, `[pageIndex]`, `[pageSize]`,
  `[pageSizeOptions]="[5, 10, 25]"` y evento `page` → actualiza `pagina` y `tamano` y recarga.
- Estados: `Loading` mientras `cargando()`; `EmptyState` con "No hay vehículos" o, con filtro activo,
  "Ningún vehículo coincide con «filtro»" y botón "Limpiar filtro"; en error, el texto "No se pudo
  cargar el listado" y un botón "Reintentar" que llama a `cargar()`.
- `vehiculos-lista.html` contiene un único `<h1>Vehículos</h1>`.

`vehiculos-lista.spec.ts` — con `provideHttpClientTesting`, `provideRouter([])` y los harnesses de
Material (`MatTableHarness`, `MatPaginatorHarness`, `MatSortHarness`, `MatInputHarness`): petición
inicial con `page=0`, `size=10`, `sort=placa,asc`; una fila por elemento; cambio de página, de tamaño
y de orden generan una petición nueva con los parámetros nuevos; el filtro espera 300 ms; estado
vacío con y sin filtro; estado de error y reintento. Espera con `await fixture.whenStable()`. Para la
prueba del `debounce` controla el tiempo con `jasmine.clock().install()`, `jasmine.clock().mockDate()`
y `jasmine.clock().tick(300)` (y `uninstall()` en `afterEach`): la app es zoneless, así que
`fakeAsync`/`tick` de Angular no están disponibles sin zone.js.

**Files**

- `frontend/src/app/features/vehiculos/lista/**` — edit: sustituye la página de relleno por `vehiculos-lista.ts`, `.html`, `.scss` y `vehiculos-lista.spec.ts`

**Acceptance**

Copiados literalmente del arreglo `acceptance` de esta tarea en `tasks.json`. Cada uno lo decide un comando de abajo, en esta máquina, durante la construcción.

1. **WHEN** `/vehiculos` loads **THE SYSTEM SHALL** request `GET /neo/vehiculos` with `page=0`, `size=10` and `sort=placa,asc` and render one table row per element of `content`, showing the placa with the CSS class `placa` and one chip for `tipo` and one for dentro or fuera.
2. **WHEN** the user changes the page or the page size in the paginator or clicks a sortable column header **THE SYSTEM SHALL** issue a new request carrying the new `page`, `size` and `sort` values instead of reordering rows in the browser.
3. **WHEN** the user types in the placa filter **THE SYSTEM SHALL** wait 300 ms after the last keystroke and then issue one request with the `placa` parameter and `page=0`.
4. **WHEN** the response has an empty `content` **THE SYSTEM SHALL** render the empty state with the text `No hay vehículos`, and with an active filter the text `Ningún vehículo coincide` plus a `Limpiar filtro` button.
5. **WHEN** the request fails **THE SYSTEM SHALL** hide the loading indicator and render `No se pudo cargar el listado` with a `Reintentar` button that repeats the request.
6. **WHEN** the page renders **THE SYSTEM SHALL** expose exactly one `h1` and a `mat-label` associated with the filter input.

**Verify** — todos los comandos, en orden, cada uno en su propio shell desde la raíz del proyecto. Cada uno sale con 0 cuando la tarea está bien; la tarea está hecha cuando el último sale con 0.

```bash
test -f frontend/src/app/features/vehiculos/lista/vehiculos-lista.spec.ts   # expect: exit 0 — la spec de este paso existe
test "$(grep -c "<h1" frontend/src/app/features/vehiculos/lista/vehiculos-lista.html)" = "1"   # expect: exit 0 — un solo h1
(cd frontend && npm run format:check)   # expect: exit 0
(cd frontend && npm run test:ci)   # expect: exit 0, 0 fallos, 0 omitidos
(cd frontend && npm run build)   # expect: exit 0
```

Son el arreglo `verify` de esta tarea en `tasks.json`, una línea por comando, con la afirmación de código de salida intacta (`...; test $? -eq 1`). No los edites ni los "simplifiques".

**Checkpoint**

```bash
git add -A && git commit -q -m "feat(frontend): listado de vehiculos con paginacion, orden y filtro en servidor [E3-T2]" -m "Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
git tag step-10-listado-vehiculos
git ls-files --error-unmatch frontend/src/app/features/vehiculos/lista/vehiculos-lista.spec.ts   # expect: exit 0
# punto de retorno si el paso siguiente sale mal: git reset --hard step-10-listado-vehiculos
```

Ejecuta el bloque después de que el último comando de Verify salga con 0 y antes de empezar la tarea siguiente. La etiqueta es el valor `checkpoint` de esta tarea en `tasks.json`; si esta tarea sale mal, vuelve con `git reset --hard step-09-frontend-base`.

### `E3-T3` — Construir formularios de operación

**Depends on:** `E3-T1` · **Priority:** p0 — metadato para recortes de alcance, no un orden de ejecución · **Paso global:** 11

**Objetivo:** La página `/operacion` permite registrar entrada, registrar salida, dar de alta oficial o residente e iniciar mes con confirmación, con validación de placa y resultado visible.

Sustituye la página de relleno de `/operacion` por los cuatro formularios que pide el enunciado. Cada
formulario es un componente pequeño dentro de una `mat-card`; `operacion.ts` solo los compone en una
rejilla (una columna bajo 960px, dos columnas por encima).

- `entrada-form.ts` y `salida-form.ts` — Reactive Form con un control `placa` (`Validators.required`,
  `placaValidator`) y la directiva `appPlacaMayusculas`. Campo: `mat-form-field` con `<mat-label>Placa</mat-label>`,
  `<mat-hint>3 a 10 letras o dígitos</mat-hint>` y `<mat-error>` con el mensaje de validación. Al
  enviar: deshabilita el botón, llama a `EstanciasApi` con `normalizarPlaca(valor)` y, al terminar,
  lo vuelve a habilitar.
  - Entrada correcta → snackbar "Entrada registrada: " seguido de la placa normalizada, y el formulario se limpia.
  - Salida correcta → bloque de resultado dentro de la tarjeta con `minutos`, `importe` (con
    `CurrencyPipe`, código `MXN`) y la etiqueta según `momentoCobro`: `AL_SALIR` → "Cobrar al salir",
    `INFORME_MENSUAL` → "Se acumula al informe mensual", `NINGUNO` → "Sin cobro".
- `alta-form.ts` — control `placa` igual que arriba y un `mat-button-toggle-group` (o `mat-select`)
  con las opciones "Oficial" y "Residente"; envía a `VehiculosApi.alta(tipo, placa)`. Éxito → snackbar
  "Vehículo ABC123 registrado como Oficial" (placa y tipo reales).
- `iniciar-mes.ts` — botón "Iniciar nuevo mes" que abre `confirmar-dialog.ts` (`MatDialog`) con el
  título "¿Iniciar un nuevo mes?", el texto "Los acumulados de residentes vuelven a cero. Esta acción
  no se puede deshacer." y los botones "Cancelar" e "Iniciar mes". Solo si el diálogo se cierra con
  `true` se llama a `MesApi.iniciar()`; éxito → snackbar "Nuevo mes iniciado".
- Errores: el interceptor ya muestra el `detail` del backend en un snackbar; el componente solo debe
  conservar el valor escrito y rehabilitar el botón.
- `operacion.html` contiene un único `<h1>Operación</h1>`; cada tarjeta usa `<h2>` para su título.

Specs (`operacion.spec.ts`, `entrada-form.spec.ts`, `salida-form.spec.ts`, `alta-form.spec.ts`,
`iniciar-mes.spec.ts`) con `provideHttpClientTesting` y harnesses (`MatInputHarness`,
`MatButtonHarness`, `MatDialogHarness`, `MatSnackBarHarness`, `MatFormFieldHarness`): los seis
criterios de aceptación. El criterio de `aria-describedby` se afirma leyendo el atributo del `input`
y comprobando que incluye el `id` del `mat-error` visible (lo cablea `MatFormField`). Espera con
`await fixture.whenStable()`.

No añadas un formulario de alta de no residentes: el enunciado pide "oficial o residente" en la
interfaz; el endpoint existe y queda accesible desde Swagger.

**Files**

- `frontend/src/app/features/operacion/**` — edit: sustituye la página de relleno por `operacion.ts` y sus componentes `entrada-form`, `salida-form`, `alta-form`, `iniciar-mes`, `confirmar-dialog`, con sus specs

**Acceptance**

Copiados literalmente del arreglo `acceptance` de esta tarea en `tasks.json`. Cada uno lo decide un comando de abajo, en esta máquina, durante la construcción.

1. **WHEN** the entrada form is submitted with a valid placa **THE SYSTEM SHALL** send `POST /neo/estancias/entrada` with the normalized placa and show a snackbar containing the placa and the text `Entrada registrada`.
2. **WHEN** the salida form receives a successful response **THE SYSTEM SHALL** display the returned `minutos` and the `importe` formatted as currency, together with the label `Cobrar al salir`, `Se acumula al informe mensual` or `Sin cobro` according to `momentoCobro`.
3. **WHEN** a placa field holds a value that does not match `^[A-Z0-9]{3,10}$` after normalization **THE SYSTEM SHALL** keep the submit button disabled and, once the field is touched, show a `mat-error` referenced by the `aria-describedby` of the input.
4. **WHEN** the alta form is submitted with tipo `OFICIAL` or `RESIDENTE` **THE SYSTEM SHALL** send the request to `/neo/vehiculos/oficiales` or `/neo/vehiculos/residentes` respectively.
5. **WHEN** the user clicks `Iniciar nuevo mes` **THE SYSTEM SHALL** open a `MatDialog` asking for confirmation and send `POST /neo/mes/iniciar` only after the user confirms, sending no request when the user cancels.
6. **WHEN** a request fails with a `ProblemDetail` **THE SYSTEM SHALL** keep the typed value in the form and enable the submit button again.

**Verify** — todos los comandos, en orden, cada uno en su propio shell desde la raíz del proyecto. Cada uno sale con 0 cuando la tarea está bien; la tarea está hecha cuando el último sale con 0.

```bash
test -f frontend/src/app/features/operacion/operacion.spec.ts && test -f frontend/src/app/features/operacion/iniciar-mes.spec.ts   # expect: exit 0 — las specs de este paso existen
test "$(grep -c "<h1" frontend/src/app/features/operacion/operacion.html)" = "1"   # expect: exit 0 — un solo h1
(cd frontend && npm run format:check)   # expect: exit 0
(cd frontend && npm run test:ci)   # expect: exit 0, 0 fallos, 0 omitidos
(cd frontend && npm run build)   # expect: exit 0
```

Son el arreglo `verify` de esta tarea en `tasks.json`, una línea por comando, con la afirmación de código de salida intacta (`...; test $? -eq 1`). No los edites ni los "simplifiques".

**Checkpoint**

```bash
git add -A && git commit -q -m "feat(frontend): formularios de entrada, salida, alta e inicio de mes [E3-T3]" -m "Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
git tag step-11-operacion
git ls-files --error-unmatch frontend/src/app/features/operacion/operacion.spec.ts   # expect: exit 0
# punto de retorno si el paso siguiente sale mal: git reset --hard step-11-operacion
```

Ejecuta el bloque después de que el último comando de Verify salga con 0 y antes de empezar la tarea siguiente. La etiqueta es el valor `checkpoint` de esta tarea en `tasks.json`; si esta tarea sale mal, vuelve con `git reset --hard step-10-listado-vehiculos`.

### `E3-T4` — Construir vista de detalle de vehículo

**Depends on:** `E3-T1` · **Priority:** p0 — metadato para recortes de alcance, no un orden de ejecución · **Paso global:** 12

**Objetivo:** La página `/vehiculos/:placa` muestra los datos del vehículo, el acumulado del periodo si es residente, la estancia abierta si está dentro y el historial paginado de estancias.

Sustituye la página de relleno de `/vehiculos/:placa` por el detalle. La placa llega por el parámetro
de ruta (`input()` con `withComponentInputBinding()` o `ActivatedRoute`); al cambiar, se recarga.

- `vehiculo-detalle.ts` — signals `vehiculo` (`VehiculoDetalleResponse | null`), `estancias`
  (`PageResponse<EstanciaResponse> | null`), `pagina` (0), `cargando`, `noEncontrado`, `error`. Al
  iniciar lanza en paralelo `VehiculosApi.detalle(placa)` y `VehiculosApi.estancias(placa, 0, 10)`.
- Cabecera: `<h1>` con la placa en `<span class="placa">`, `EstadoChip` del tipo y `EstadoChip`
  `DENTRO`/`FUERA`, y un enlace "Volver al listado".
- Tarjeta "Acumulado del periodo" (`@if (vehiculo()?.acumuladoPeriodo; as acumulado)`): minutos e
  importe con `CurrencyPipe` (`MXN`). Solo existe para residentes porque el backend envía `null` en
  los demás tipos: **no** compares el tipo en la plantilla.
- Tarjeta "Estancia abierta" (`@if (vehiculo()?.estanciaAbierta; as abierta)`): fecha y hora de entrada.
- Tabla de estancias: columnas Entrada, Salida ("—" si sigue abierta), Minutos, Importe, y
  `mat-paginator` de servidor (`page` → recarga solo las estancias). Sin filas: `EmptyState` con "Este
  vehículo aún no tiene estancias".
- 404 del detalle → `noEncontrado` en `true` y se renderiza solo el bloque "Vehículo no encontrado"
  con un enlace a `/vehiculos`. El snackbar del interceptor aparece igualmente; es aceptable.
- Otro error → "No se pudo cargar el vehículo" + "Reintentar".
- `vehiculo-detalle.html` contiene un único `<h1>` en total: colócalo fuera de los bloques
  condicionales (la placa de la ruta siempre está disponible) para que exista en todos los estados.

`vehiculo-detalle.spec.ts` — con `provideHttpClientTesting` y `provideRouter`: las dos peticiones
iniciales; residente con tarjeta de acumulado y no residente sin ella; tarjeta de estancia abierta;
cambio de página del historial; 404 → bloque "Vehículo no encontrado"; historial vacío.

**Files**

- `frontend/src/app/features/vehiculos/detalle/**` — edit: sustituye la página de relleno por `vehiculo-detalle.ts`, `.html`, `.scss` y `vehiculo-detalle.spec.ts`

**Acceptance**

Copiados literalmente del arreglo `acceptance` de esta tarea en `tasks.json`. Cada uno lo decide un comando de abajo, en esta máquina, durante la construcción.

1. **WHEN** `/vehiculos/RES001` loads **THE SYSTEM SHALL** request `GET /neo/vehiculos/RES001` and `GET /neo/vehiculos/RES001/estancias` with `page=0` and `size=10` and render the placa, the tipo chip and the dentro or fuera chip.
2. **WHEN** the vehicle is `RESIDENTE` **THE SYSTEM SHALL** show the card `Acumulado del periodo` with `minutos` and `importe`, and for any other tipo THE SYSTEM SHALL not render that card.
3. **WHEN** `estanciaAbierta` is not null **THE SYSTEM SHALL** show the card `Estancia abierta` with its entrada date.
4. **WHEN** the estancias paginator changes page **THE SYSTEM SHALL** request the new `page` from the server.
5. **WHEN** the detail request returns 404 **THE SYSTEM SHALL** render `Vehículo no encontrado` with a link to `/vehiculos` instead of the detail cards.
6. **WHEN** the estancias list is empty **THE SYSTEM SHALL** render `Este vehículo aún no tiene estancias`.

**Verify** — todos los comandos, en orden, cada uno en su propio shell desde la raíz del proyecto. Cada uno sale con 0 cuando la tarea está bien; la tarea está hecha cuando el último sale con 0.

```bash
test -f frontend/src/app/features/vehiculos/detalle/vehiculo-detalle.spec.ts   # expect: exit 0 — la spec de este paso existe
test "$(grep -c "<h1" frontend/src/app/features/vehiculos/detalle/vehiculo-detalle.html)" = "1"   # expect: exit 0 — un solo h1
(cd frontend && npm run format:check)   # expect: exit 0
(cd frontend && npm run test:ci)   # expect: exit 0, 0 fallos, 0 omitidos
(cd frontend && npm run build)   # expect: exit 0
```

Son el arreglo `verify` de esta tarea en `tasks.json`, una línea por comando, con la afirmación de código de salida intacta (`...; test $? -eq 1`). No los edites ni los "simplifiques".

**Checkpoint**

```bash
git add -A && git commit -q -m "feat(frontend): detalle de vehiculo con acumulado e historial de estancias [E3-T4]" -m "Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
git tag step-12-detalle-vehiculo
git ls-files --error-unmatch frontend/src/app/features/vehiculos/detalle/vehiculo-detalle.spec.ts   # expect: exit 0
# punto de retorno si el paso siguiente sale mal: git reset --hard step-12-detalle-vehiculo
```

Ejecuta el bloque después de que el último comando de Verify salga con 0 y antes de empezar la tarea siguiente. La etiqueta es el valor `checkpoint` de esta tarea en `tasks.json`; si esta tarea sale mal, vuelve con `git reset --hard step-11-operacion`.

### `E3-T5` — Construir reporte de residentes con descarga

**Depends on:** `E3-T1` · **Priority:** p0 — metadato para recortes de alcance, no un orden de ejecución · **Paso global:** 13

**Objetivo:** La página `/reporte` muestra el informe de pagos del periodo activo con su total y descarga el Excel generado por el backend.

Sustituye la página de relleno de `/reporte` por el informe de pagos de residentes del periodo activo.

- `reporte.ts` — signals `informe` (`InformePagosResponse | null`), `cargando`, `error`,
  `descargando`. `cargar()` llama a `InformeApi.obtener()`.
- Cabecera: `<h1>Reporte de residentes</h1>`, el texto "Periodo iniciado el" seguido de la fecha, con
  `periodo.inicio` formateado con `DatePipe`, y el botón "Descargar Excel" (deshabilitado si no hay
  filas o mientras descarga).
- Tabla `mat-table`: columnas Placa (`class="placa"`), Minutos, Importe (`CurrencyPipe`, `MXN`), y una
  fila de pie (`mat-footer-row`) con "Total", `totalMinutos` y `totalImporte`.
- Descarga: `InformeApi.descargarExcel()` devuelve `HttpResponse<Blob>`; el nombre se extrae de la
  cabecera `Content-Disposition` con una expresión regular sobre `filename="..."` y, si no está,
  se usa `informe-pagos.xlsx`. Se crea un `URL.createObjectURL(blob)`, se pulsa un `<a download>`
  creado en memoria y se libera con `URL.revokeObjectURL`.
- Estados: `Loading`; vacío → `EmptyState` "No hay residentes registrados"; error → "No se pudo cargar
  el informe" + "Reintentar".
- `reporte.html` contiene un único `<h1>`.

`reporte.spec.ts` — petición JSON inicial con `Accept: application/json`; filas y fila de total; clic
en "Descargar Excel" → petición con el `Accept` de hoja de cálculo y `responseType` `blob` (responde
con un `Blob` y la cabecera `Content-Disposition`; espía `URL.createObjectURL` y comprueba el atributo
`download` del enlace); sin cabecera → `informe-pagos.xlsx`; vacío con botón deshabilitado; error con
reintento.

**Files**

- `frontend/src/app/features/reporte/**` — edit: sustituye la página de relleno por `reporte.ts`, `.html`, `.scss` y `reporte.spec.ts`

**Acceptance**

Copiados literalmente del arreglo `acceptance` de esta tarea en `tasks.json`. Cada uno lo decide un comando de abajo, en esta máquina, durante la construcción.

1. **WHEN** `/reporte` loads **THE SYSTEM SHALL** request `GET /neo/residentes/pagos` with `Accept: application/json` and render one row per element of `filas` plus a total row showing `totalMinutos` and `totalImporte`.
2. **WHEN** the user clicks `Descargar Excel` **THE SYSTEM SHALL** request `GET /neo/residentes/pagos` with `Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` and response type `blob`, and trigger a download named after the `Content-Disposition` header, falling back to `informe-pagos.xlsx`.
3. **WHEN** `filas` is empty **THE SYSTEM SHALL** render `No hay residentes registrados` and disable the download button.
4. **WHEN** the request fails **THE SYSTEM SHALL** render `No se pudo cargar el informe` with a `Reintentar` button.
5. **WHEN** the page renders **THE SYSTEM SHALL** show the start date of the period taken from `periodo.inicio` and exactly one `h1`.

**Verify** — todos los comandos, en orden, cada uno en su propio shell desde la raíz del proyecto. Cada uno sale con 0 cuando la tarea está bien; la tarea está hecha cuando el último sale con 0.

```bash
test -f frontend/src/app/features/reporte/reporte.spec.ts   # expect: exit 0 — la spec de este paso existe
test "$(grep -c "<h1" frontend/src/app/features/reporte/reporte.html)" = "1"   # expect: exit 0 — un solo h1
(cd frontend && npm run format:check)   # expect: exit 0
(cd frontend && npm run test:ci)   # expect: exit 0, 0 fallos, 0 omitidos
(cd frontend && npm run build)   # expect: exit 0
```

Son el arreglo `verify` de esta tarea en `tasks.json`, una línea por comando, con la afirmación de código de salida intacta (`...; test $? -eq 1`). No los edites ni los "simplifiques".

**Checkpoint**

```bash
git add -A && git commit -q -m "feat(frontend): reporte de residentes con descarga de Excel [E3-T5]" -m "Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
git tag step-13-reporte-residentes
git ls-files --error-unmatch frontend/src/app/features/reporte/reporte.spec.ts   # expect: exit 0
# punto de retorno si el paso siguiente sale mal: git reset --hard step-13-reporte-residentes
```

Ejecuta el bloque después de que el último comando de Verify salga con 0 y antes de empezar la tarea siguiente. La etiqueta es el valor `checkpoint` de esta tarea en `tasks.json`; si esta tarea sale mal, vuelve con `git reset --hard step-12-detalle-vehiculo`.

---

## Epic acceptance

The epic is done when every task is `done` **and**:

1. **WHEN** `npm run format:check`, `npm run test:ci` and `npm run build` run in `frontend/` **THE SYSTEM SHALL** exit 0 on all three, with 0 failed and 0 skipped specs.
2. **WHEN** the five page templates are inspected **THE SYSTEM SHALL** contain exactly one `<h1` each, and **WHEN** `frontend/src/app` is searched for `innerHTML` **THE SYSTEM SHALL** find no match.

```bash
(cd frontend && npm run format:check && npm run test:ci && npm run build)
test "$(grep -c "<h1" frontend/src/app/features/vehiculos/lista/vehiculos-lista.html)" = "1" && test "$(grep -c "<h1" frontend/src/app/features/vehiculos/detalle/vehiculo-detalle.html)" = "1" && test "$(grep -c "<h1" frontend/src/app/features/operacion/operacion.html)" = "1" && test "$(grep -c "<h1" frontend/src/app/features/reporte/reporte.html)" = "1" && test "$(grep -c "<h1" frontend/src/app/features/no-encontrado/no-encontrado.html)" = "1"
grep -rl "innerHTML" frontend/src/app; test $? -eq 1
```

Run from the project root. Both criteria must be decidable by these commands.

## Pitfalls

- **`ng test` sin `--no-watch`** — se queda esperando y bloquea la construcción; usa siempre `npm run test:ci`.
- **Aserciones antes de `whenStable()`** — el DOM aún no refleja la señal; pruebas intermitentes.
- **Filtro con `debounceTime` probado sin `jasmine.clock().mockDate()`** — RxJS mira `Date.now()`;
  sin `mockDate` el `tick` no dispara.
- **Especificar `Accept` para el Excel sin `responseType: 'blob'`** — el `HttpClient` intentaría
  parsear JSON y fallaría.
- **Comparar `tipo === 'RESIDENTE'` en el detalle** — el backend ya envía `acumuladoPeriodo` en
  `null` para los demás; condiciona por el dato, no por el tipo.
- **Un segundo `h1` en un bloque `@if`** — la spec de accesibilidad cuenta exactamente uno en todos
  los estados; deja el `h1` fuera de los condicionales.

## Before moving on

- [ ] Every task in this epic is `done` in `tasks.json` — no task left `in_progress`.
- [ ] Every `verify` command of every task in this epic passed, not just the first one.
- [ ] No `verify` command was edited, and none was skipped because a file it names did not exist.
- [ ] **Every task in this epic has its `checkpoint` tag in version control** — one tag per task,
      matching the `checkpoint` value in `tasks.json`. `git tag -l 'step-*'` lists them.
- [ ] Gate command passes clean, run from the project root.
- [ ] Every "Produced" contract above exists with the stated signature.
- [ ] No file outside the subtree was modified.
- [ ] This project has no `.env.example`: no environment variable was added (`CHROME_BIN` es opcional y solo del shell).
- [ ] One commit per task, each carrying its task id, each followed by its checkpoint tag.
