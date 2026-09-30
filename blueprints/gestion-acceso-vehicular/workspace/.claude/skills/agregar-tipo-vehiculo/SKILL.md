---
name: agregar-tipo-vehiculo
description: Procedimiento para añadir un nuevo tipo de vehículo con su tarifa (por ejemplo "visitante" o "proveedor") usando el patrón Strategy, sin tocar los casos de uso. Úsala cuando pidan "agrega un tipo de vehículo", "nueva tarifa", "nuevo tipo de cobro" o "soporta vehículos de X".
---

# Agregar un tipo de vehículo

## When to use

- Se necesita un tipo de vehículo nuevo con su propia tarifa o su propio momento de cobro.

## Steps

1. **Enum** — añade la constante a `backend/src/main/java/mx/neology/acceso/domain/TipoVehiculo.java`
   (`provisional = false` salvo que sea el tipo que reciben las placas sin alta).
2. **Estrategia** — crea en `domain/cobro/` una clase que implemente `EstrategiaCobro`: devuelve el
   tipo nuevo en `tipo()`, el `MomentoCobro` que corresponda en `momento()` y el importe en
   `calcular(long minutos)` (usa `Importes.porMinuto`).
3. **Tarifa** — añade la clave bajo `acceso.tarifas` en `backend/src/main/resources/application.yml`
   con el nombre del tipo en minúsculas y guiones (`VISITANTE` → `visitante`, `NO_RESIDENTE` →
   `no-residente`). `TarifasProperties` la resuelve sola con `porMinuto(tipo)`; no hay que tocarla.
4. **Bean** — registra la estrategia en `infrastructure/config/CobroConfig.java` junto a las demás
   (`new CobroVisitante(tarifas.porMinuto(TipoVehiculo.VISITANTE))`); `CalculadoraCobro` la recoge
   sola y, si falta, el arranque falla con `IllegalStateException` nombrando el tipo.
5. **Alta (opcional)** — si el tipo debe darse de alta a mano, añade `POST /neo/vehiculos/<plural>` en
   `VehiculosController`, delegando en `RegistrarVehiculo`. Si solo entra por la puerta (como los no
   residentes), no hace falta.
6. **Pruebas** — amplía `CalculadoraCobroTest` con los casos del tipo nuevo (y `AltasIntegracionTest`
   si añadiste endpoint).
7. **Frontend** — añade el valor a `TipoVehiculo` en `frontend/src/app/core/modelos/modelos.ts`, su
   texto y color en `shared/estado-chip/estado-chip.ts` y, si debe poder darse de alta desde la
   interfaz, la opción en `features/operacion/alta-form.ts`.

No hace falta migración: la columna `vehiculos.tipo` no tiene `CHECK`; el valor lo garantiza el enum.

## Verify

```bash
grep -rnE "switch ?\(.*[Tt]ipo|== ?TipoVehiculo\." backend/src/main/java/mx/neology/acceso/application backend/src/main/java/mx/neology/acceso/web; test $? -eq 1   # expect: código 1 = ningún if/switch por tipo → la línea sale con 0
(cd backend && ./mvnw -q -B -ntp verify)   # expect: exit 0
(cd frontend && npm run test:ci)           # expect: exit 0
```

## Do not

- No añadas `if`/`switch` sobre `TipoVehiculo` en casos de uso, controladores ni adaptadores.
- No escribas la tarifa como literal en el código: vive en `application.yml`.
- No edites una migración ya confirmada (y no añadas un `CHECK` de tipos: obligaría a migrar por cada tipo).
