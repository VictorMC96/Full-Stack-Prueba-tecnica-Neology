---
description: Convenciones del frontend Angular (estructura, estado, formularios, estilos y pruebas)
paths:
  - "frontend/**"
---

# Frontend — convenciones

## Estructura

- Componentes standalone; archivos sin sufijo `.component` (`vehiculos-lista.ts`, clase
  `VehiculosLista`), cada página con plantilla `.html` y estilos `.scss` externos y su `.spec.ts` al lado.
- `features/*` importa de `core` y `shared`; nunca de otra feature. `core` y `shared` no importan de
  `features`. Imports relativos: no hay alias de rutas.
- Solo `core/api/*-api.ts` inyecta `HttpClient`, con rutas relativas (`/neo/...`). El proxy de
  desarrollo (`proxy.conf.json`) las lleva a `http://localhost:8080`.
- Tipos de la API en `core/modelos/modelos.ts`; deben coincidir con el contrato del backend.
- Rutas en `app.routes.ts` con `loadComponent`. No añadas rutas fuera del manifiesto.

## Estado y formularios

- Estado de página en `signal`s del componente (`datos`, `cargando`, `error`). Sin store global.
- Plantillas con `@if` / `@for` (con `track`); sin `*ngIf` ni `*ngFor`.
- Reactive Forms, nunca Signal Forms ni formularios por plantilla. Campo de placa: `placaValidator` +
  directiva `appPlacaMayusculas`; envía siempre `normalizarPlaca(valor)`.
- El servidor pagina, ordena y filtra: no uses orden ni filtro de `MatTableDataSource`.
- Errores HTTP: los muestra `problemDetailInterceptor` en un `MatSnackBar`; el componente solo pasa a
  su estado de error, conserva lo escrito y rehabilita el botón. Ramifica por `code`, no por `detail`.

## Interfaz y accesibilidad

- Textos en español. Un solo `h1` por página; títulos de tarjeta en `h2`.
- Toda lista tiene estado de carga (`Loading`), vacío (`EmptyState`) y error con "Reintentar".
- Todo `input` dentro de `mat-form-field` con `mat-label`; errores en `mat-error`. Botones de solo
  icono con `aria-label`. No quites el `outline` de foco.
- Placas con la clase `.placa`. Estados con `EstadoChip` (color + texto, nunca solo color).
- Sin `innerHTML`, sin `bypassSecurityTrust*`.

## Estilos

- Material 3 con `mat.theme` en `src/styles.scss`; ese archivo es el único con colores hexadecimales.
- En componentes: `var(--app-...)` o `var(--mat-sys-...)`. Espaciado solo con `--app-space-*`.
- Un solo mecanismo de estilo: SCSS + Material. Sin Tailwind ni CSS-in-JS. Una sola familia de
  iconos: Material Symbols Outlined.

## Pruebas (Karma + Jasmine, ChromeHeadless)

- La app es zoneless: espera con `await fixture.whenStable()`. `fakeAsync`/`tick` no están
  disponibles; para temporizadores usa `jasmine.clock().install()`, `mockDate()` y `tick(ms)`, y
  `uninstall()` en `afterEach`.
- HTTP: `provideHttpClient()` + `provideHttpClientTesting()` y `HttpTestingController`; afirma método,
  URL y parámetros, y llama a `verify()` al final.
- Material: usa los harnesses (`TestbedHarnessEnvironment.loader(fixture)`), no selectores de clases
  internas de Material.
- Rutas: `provideRouter([...])` y `RouterTestingHarness`.
- Comando: `(cd frontend && npm run test:ci)`. Si Karma no encuentra Chrome:
  `export CHROME_BIN="/c/Program Files/Google/Chrome/Application/chrome.exe"`.
- Antes de verificar: `(cd frontend && npm run format)`.
