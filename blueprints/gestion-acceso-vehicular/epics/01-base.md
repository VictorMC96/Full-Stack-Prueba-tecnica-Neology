# Epic 01: Base del repositorio

> Al terminar esta épica existen el README esqueleto y el registro de decisiones, y está comprobado que
> Bootstrap dejó la rama, el remoto y las reglas de git correctas.

| | |
|---|---|
| **Epic id** | `01-base` |
| **Tasks** | `E1-T1` |
| **Depends on** | nothing — start here (solo requiere que el bloque Bootstrap de `blueprint.md` §10 se haya ejecutado) |
| **Unlocks** | `04-calidad-entrega` (el paso 15 completa el README que aquí nace) |
| **Parallel with** | `02-backend`, `03-frontend` — no comparten archivos |

You do not need any other file to complete this epic. Everything below is repeated here on purpose.

---

## Stack

Java 21 · Spring Boot 4 · H2 en memoria · Flyway · Angular 22 · Angular Material · SCSS · Karma +
Jasmine · sin autenticación · sin despliegue. Shell: **Git Bash**, desde la raíz del proyecto.
Maven no está instalado: siempre `./mvnw`. Las versiones viven en `backend/pom.xml` y
`frontend/package.json` — léelas, nunca las adivines.

| Task | Command |
|---|---|
| Backend — puerta | `(cd backend && ./mvnw -q -B -ntp verify)` |
| Frontend — build | `(cd frontend && npm run build)` |
| Estado de git | `git status --short` · `git tag -l 'step-*'` |

**Gate:** esta épica no toca código; su puerta son los comandos `Verify` de la tarea. Cada línea de
`Verify` se ejecuta en su propio shell desde la raíz del proyecto.

No hay servicios locales que arrancar.

## Directory subtree

Only the parts this epic touches:

```
./
  README.md              # exists (heredado de upstream/main) — lo sustituyes por el esqueleto
  docs/
    decisiones.md        # NEW — decisiones técnicas en formato ADR corto
  .gitignore             # exists (llegó de workspace/ en Bootstrap) — solo lectura aquí
  .gitattributes         # exists — solo lectura aquí
  backend/mvnw           # exists (Spring Initializr) — solo se comprueba su atributo eol
  .claude/settings.json  # exists (workspace/) — solo se comprueba que no está ignorado
```

Everything outside this subtree is out of scope. If a task seems to require editing a file not
listed here, stop and report — it means the epic boundary is wrong.

## Data model touched here

| Entity | Fields this epic adds or reads | Notes |
|---|---|---|
| — | ninguno | Esta épica no toca la base de datos |

## Contracts

**Consumed** — already exists, do not rebuild:

| From | Interface | Guarantee |
|---|---|---|
| Bootstrap (`blueprint.md` §10) | rama `feature/xochitlgr`, remoto `upstream`, `.gitignore`, `.gitattributes`, `backend/`, `frontend/`, primer commit | Todo existe antes de `E1-T1`; si falta algo, se vuelve a ejecutar Bootstrap, no se recrea a mano |

**Produced** — later epics depend on exactly these signatures. Changing one breaks them:

| Export | Signature | Used by |
|---|---|---|
| `README.md` | Secciones `## Cómo levantar` y `## Pruebas` con los comandos `./mvnw spring-boot:run`, `npm ci`, `npm start`, `./mvnw verify`, `npm run test:ci` | `04-calidad-entrega` (E4-T2 lo completa sin renombrar secciones) |
| `docs/decisiones.md` | Lista numerada de decisiones (Contexto / Decisión / Consecuencia) | `04-calidad-entrega` (E4-T2 lo enlaza desde el README y lo revisa) |

## Conventions that bite in this area

- Los archivos `.gitignore`, `.gitattributes` y `.editorconfig` **ya están en la raíz**: los copió
  Bootstrap desde `blueprints/gestion-acceso-vehicular/workspace/`. No los crees ni los edites.
- No escribas la versión de Spring Boot de memoria: léela de `<parent><version>` en `backend/pom.xml`.
- El README se escribe en español. Los comandos se muestran con `cd backend && ...` y
  `cd frontend && ...` para que un evaluador los copie; en tus propias comprobaciones usa la forma
  `(cd backend && ...)`.
- Commit: Conventional Commits sin acentos, con la línea `Co-Authored-By` del bloque Checkpoint.

Full project rules: `CLAUDE.md`. Area rules: `.claude/rules/backend.md`, `.claude/rules/frontend.md`.
All sit in the project root — the builder copied them there from the bundle's `workspace/` before task one.

---

## Tasks

Listed in the same order as `tasks.json`. That order is the build order — work top to bottom and do
not re-rank by priority or by what looks quick.

### `E1-T1` — Crear README base y registro de decisiones

**Depends on:** nada — solo el Bootstrap · **Priority:** p0 — metadato para recortes de alcance, no un orden de ejecución · **Paso global:** 1

**Objetivo:** Existen el README esqueleto en español y `docs/decisiones.md`, y queda comprobado que la rama, el remoto y las reglas de ignore y de fin de línea que dejó Bootstrap son las correctas.

Bootstrap ya dejó el repositorio en la rama `feature/xochitlgr`, con el remoto `upstream` y con
`.gitignore`, `.gitattributes` y `.editorconfig` en la raíz (llegaron con la copia de `workspace/`
antes del primer commit; **este paso no los crea ni los edita**, solo comprueba su efecto). Aquí se
escribe la documentación mínima que acompaña al resto de la construcción.

`README.md` — sustituye el contenido heredado de `upstream/main` por un esqueleto en español; el paso
15 lo completa. Estructura obligatoria:

```markdown
# Gestión de Acceso Vehicular

Sistema web para gestionar el acceso de vehículos a un estacionamiento: entradas, salidas, cobros y
reporte mensual. Prueba técnica Full Stack para Neology.

## Stack
Backend: Java 21 · Spring Boot · Spring Data JPA · H2 en memoria · Flyway.
Frontend: Angular · Angular Material · SCSS.

## Cómo levantar
    cd backend && ./mvnw spring-boot:run        # API en http://localhost:8080
    cd frontend && npm ci && npm start          # interfaz en http://localhost:4200

## Pruebas
    cd backend && ./mvnw verify
    cd frontend && npm run test:ci

## Decisiones técnicas
Ver `docs/decisiones.md` y la sección "Decisiones técnicas" de `Requerimientos.md`.
```

`docs/decisiones.md` — una lista de decisiones en formato corto (Contexto / Decisión / Consecuencia,
dos o tres líneas cada una). Debe incluir, como mínimo, estas quince:

1. **Spring Boot 4 en lugar de 3.x** — Spring Initializr ya no ofrece 3.x y esa línea no tiene soporte
   OSS. Escribe la versión exacta leyéndola de `<parent><version>` en `backend/pom.xml`; no la escribas
   de memoria.
2. **Patrón Strategy para el cobro** — una estrategia por tipo, sin `if`/`switch` por tipo en servicios.
3. **Importes en `BigDecimal` / `DECIMAL(12,2)`** — decisión de `Requerimientos.md`; es una desviación
   consciente de la regla general "dinero como entero en unidades mínimas".
4. **Minutos completos** — se truncan los segundos (`Duration.toMinutes()`).
5. **Placa no registrada = no residente** — alta automática en la primera entrada y reclasificación posterior.
6. **Reinicio de mes por periodos** — las estancias se archivan con su periodo; nada se borra.
7. **Informe en JSON y Excel** — negociación por la cabecera `Accept` sobre la misma ruta.
8. **Errores RFC 9457** — `ProblemDetail` con `code` estable y `errors` por campo.
9. **Concurrencia** — bloqueo pesimista sobre el vehículo, más columnas únicas anulables porque H2 no
   tiene índices únicos parciales.
10. **Reloj inyectado** — `java.time.Clock` con zona `America/Mexico_City`; el servidor asigna toda fecha.
11. **Arquitectura limpia** — paquetes `domain`, `application`, `infrastructure`, `web`; dominio y
    aplicación sin Spring.
12. **Sin Lombok** — `record` de Java 21.
13. **Angular con Reactive Forms, zoneless y Karma** — Karma pedido por el enunciado.
14. **Sin autenticación** — no la pide el enunciado.
15. **Rutas literales del enunciado** — `/neo/...`, sin prefijo de versión.

No escribas ni edites ningún otro archivo en este paso.

**Files**

- `README.md` — edit: sustituye el contenido heredado de `upstream/main` por el esqueleto
- `docs/decisiones.md` — new: decisiones técnicas en formato ADR corto

**Acceptance**

Copiados literalmente del arreglo `acceptance` de esta tarea en `tasks.json`. Cada uno lo decide un comando de abajo, en esta máquina, durante la construcción.

1. **WHEN** `git rev-parse --abbrev-ref HEAD` runs **THE SYSTEM SHALL** print `feature/xochitlgr`.
2. **WHEN** `git remote get-url upstream` runs **THE SYSTEM SHALL** print `https://github.com/VictorMC96/Full-Stack-Prueba-tecnica-Neology.git`.
3. **WHEN** `git check-attr eol -- backend/mvnw` runs **THE SYSTEM SHALL** report `eol: lf`.
4. **WHEN** `git check-ignore -q backend/target/x.class` runs **THE SYSTEM SHALL** exit 0, because build output is ignored.
5. **WHEN** `git check-ignore -q .claude/settings.json` runs **THE SYSTEM SHALL** exit 1, because the agent workspace is committed.
6. **WHEN** `README.md` and `docs/decisiones.md` are read **THE SYSTEM SHALL** find the headings `## Cómo levantar` and `## Pruebas` in the first and the text `Spring Boot 4` in the second.

**Verify** — todos los comandos, en orden, cada uno en su propio shell desde la raíz del proyecto. Cada uno sale con 0 cuando la tarea está bien; la tarea está hecha cuando el último sale con 0.

```bash
test "$(git rev-parse --abbrev-ref HEAD)" = "feature/xochitlgr"   # expect: exit 0 — la rama de trabajo es la que creó Bootstrap
test "$(git remote get-url upstream)" = "https://github.com/VictorMC96/Full-Stack-Prueba-tecnica-Neology.git"   # expect: exit 0
git check-attr eol -- backend/mvnw | grep -q "eol: lf"   # expect: exit 0 — .gitattributes fuerza LF en el wrapper
git check-ignore -q backend/target/x.class   # expect: exit 0 — 0 = ignorado
git check-ignore -q .claude/settings.json; test $? -eq 1   # expect: código 1 = no ignorado → la línea sale con 0 (128 sería error de uso y falla)
grep -q "## Cómo levantar" README.md && grep -q "## Pruebas" README.md   # expect: exit 0
grep -q "Spring Boot 4" docs/decisiones.md   # expect: exit 0
```

Son el arreglo `verify` de esta tarea en `tasks.json`, una línea por comando, con la afirmación de código de salida intacta (`...; test $? -eq 1`). No los edites ni los "simplifiques".

**Checkpoint**

```bash
git add -A && git commit -q -m "docs: README base y registro de decisiones [E1-T1]" -m "Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
git tag step-01-repo-base
git ls-files --error-unmatch README.md   # expect: exit 0 — confirmado en el commit de arriba
git ls-files --error-unmatch docs/decisiones.md   # expect: exit 0
# punto de retorno si el paso siguiente sale mal: git reset --hard step-01-repo-base
```

Ejecuta el bloque después de que el último comando de Verify salga con 0 y antes de empezar la tarea siguiente. La etiqueta es el valor `checkpoint` de esta tarea en `tasks.json`.

---

## Epic acceptance

The epic is done when every task is `done` **and**:

1. **WHEN** `git rev-parse --abbrev-ref HEAD` runs **THE SYSTEM SHALL** print `feature/xochitlgr`, and `git remote get-url upstream` **SHALL** print `https://github.com/VictorMC96/Full-Stack-Prueba-tecnica-Neology.git`.
2. **WHEN** `git check-ignore -q Requerimientos.txt` runs **THE SYSTEM SHALL** exit 0, and **WHEN** `git check-ignore -q blueprints/gestion-acceso-vehicular/blueprint.md` runs **THE SYSTEM SHALL** exit 1 — el duplicado se ignora y el blueprint se versiona.

```bash
test "$(git rev-parse --abbrev-ref HEAD)" = "feature/xochitlgr" && test "$(git remote get-url upstream)" = "https://github.com/VictorMC96/Full-Stack-Prueba-tecnica-Neology.git"
git check-ignore -q Requerimientos.txt
git check-ignore -q blueprints/gestion-acceso-vehicular/blueprint.md; test $? -eq 1
```

Run from the project root. Both criteria must be decidable by these commands.

## Pitfalls

- **Crear `.gitignore` en esta épica** — ya existe desde Bootstrap; crearlo de nuevo puede pisar las
  reglas y, peor, sugiere que el primer commit se hizo sin él.
- **Escribir "Spring Boot 3.x" en el README o en las decisiones** — el proyecto usa la línea 4;
  documenta el motivo (Initializr ya no ofrece 3.x ni tiene soporte OSS).
- **Afirmar un archivo rastreado en `Verify`** — el commit ocurre en el Checkpoint; las comprobaciones
  `git ls-files --error-unmatch` van después del commit, donde ya están.

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
