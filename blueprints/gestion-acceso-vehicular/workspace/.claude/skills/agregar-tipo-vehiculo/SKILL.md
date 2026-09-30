---
name: agregar-tipo-vehiculo
description: Procedimiento para añadir un nuevo tipo de vehículo con su tarifa (por ejemplo "visitante" o "proveedor") usando el patrón Strategy, sin tocar los casos de uso. Úsala cuando pidan "agrega un tipo de vehículo", "nueva tarifa", "nuevo tipo de cobro" o "soporta vehículos de X".
---

# Agregar un tipo de vehículo

## When to use

- Se necesita un tipo de vehículo nuevo con su propia tarifa o su propio momento de cobro.

## Steps

1. **Enum** — añade la constante a `backend/src/main/java/mx/neology/acceso/domain/TipoVehiculo.java`.
2. **Estrategia** — crea en `domain/cobro/` una clase que implemente `EstrategiaCobro`: devuelve el
   tipo nuevo en `tipo()`, el `MomentoCobro` que corresponda en `momento()` y el importe en
   `calcular(long minutos)` con `setScale(2, RoundingMode.HALF_UP)`.
3. **Tarifa** — añade la clave bajo `acceso.tarifas` en
   `backend/src/main/resources/application.yml` y el campo correspondiente en `TarifasProperties`.
4. **Bean** — registra la estrategia en `infrastructure/config/` junto a las demás; `CalculadoraCobro`
   la recoge sola. Si falta, el arranque falla con `IllegalStateException` nombrando el tipo.
5. **Esquema** — crea una migración Flyway nueva (`V<n+1>__<descripcion>.sql`) que sustituya la
   restricción `ck_vehiculos_tipo` para admitir el valor nuevo. No edites `V1__esquema.sql`.
6. **Alta** — añade el endpoint `POST /neo/vehiculos/<plural-del-tipo>` en `VehiculosController`,
   delegando en `RegistrarVehiculo`.
7. **Pruebas** — amplía `CalculadoraCobroTest` con los casos del tipo nuevo y añade el caso de alta
   a `AltasIntegracionTest`.
8. **Frontend** — añade el valor a `TipoVehiculo` en `frontend/src/app/core/modelos/modelos.ts`, su
   texto y color en `shared/estado-chip/estado-chip.ts` y, si debe poder darse de alta desde la
   interfaz, la opción en `features/operacion/alta-form.ts`.

## Verify

```bash
grep -rnE "switch ?\(.*[Tt]ipo|== ?TipoVehiculo\." backend/src/main/java/mx/neology/acceso/application backend/src/main/java/mx/neology/acceso/web; test $? -eq 1   # expect: código 1 = ningún if/switch por tipo → la línea sale con 0
(cd backend && ./mvnw -q -B -ntp verify)   # expect: exit 0
(cd frontend && npm run test:ci)           # expect: exit 0
```

## Do not

- No añadas `if`/`switch` sobre `TipoVehiculo` en casos de uso, controladores ni adaptadores.
- No escribas la tarifa como literal en el código: vive en `application.yml`.
- No edites una migración ya confirmada.
