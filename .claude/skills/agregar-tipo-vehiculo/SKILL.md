---
name: agregar-tipo-vehiculo
description: Procedimiento para añadir un nuevo tipo de vehículo con su tarifa (por ejemplo "visitante" o "proveedor") o un nuevo modelo de cobro, usando el catálogo administrable y el patrón Strategy por modelo de cobro. Úsala cuando pidan "agrega un tipo de vehículo", "nueva tarifa", "nuevo tipo de cobro" o "soporta vehículos de X".
---

# Agregar un tipo de vehículo

## When to use

- Se necesita un tipo de vehículo nuevo con su propia tarifa (caso habitual: **no se toca código**).
- Se necesita un **modelo de cobro** nuevo (cuánto/cuándo se cobra) que ninguna estrategia cubre.

## Cómo está montado

- Los tipos son **filas** de `tipos_vehiculo` (`codigo`, `nombre`, `modelo_cobro`, `tarifa_por_minuto`,
  `provisional`, `integrado`), creadas en `V3__tipos_vehiculo.sql` y administrables por la API
  `/neo/tipos-vehiculo` (y desde la pantalla de tipos en Angular). `vehiculos.tipo` es clave foránea
  al código del tipo.
- El patrón Strategy vive por **modelo de cobro**: `domain/ModeloCobro` (enum) ↔ una clase
  `domain/cobro/*` que implementa `EstrategiaCobro` (`SinCobro`, `PorMinutoAlSalir`,
  `PorMinutoAcumulado`). `CalculadoraCobro` las indexa por modelo y falla al arrancar si falta una.
- Los integrados (`OFICIAL`, `RESIDENTE`, `NO_RESIDENTE`) no se eliminan ni cambian de modelo; solo
  `NO_RESIDENTE` es provisional (lo reciben las placas que entran sin alta).

## Steps — tipo nuevo (sin código)

1. **Crear el tipo** por HTTP (o desde la UI de tipos):
   `POST /neo/tipos-vehiculo` `{"codigo":"VISITANTE","nombre":"Visitante","modeloCobro":"POR_MINUTO_AL_SALIR","tarifaPorMinuto":0.30}`
   → 201. Códigos: `^[A-Z][A-Z0-9_]{2,19}$` (se normaliza a mayúsculas); `SIN_COBRO` exige tarifa 0.00.
2. **Usarlo**: `POST /neo/vehiculos` `{"placa":"VIS001","tipo":"VISITANTE"}`; entrada y salida
   cobran con la tarifa del tipo y el momento de su modelo. Si el modelo es `POR_MINUTO_ACUMULADO`,
   el tipo aparece en `GET /neo/residentes/pagos`.
3. **Ajustar o retirar**: `PUT /neo/tipos-vehiculo/{codigo}` (nombre, modelo, tarifa) y
   `DELETE /neo/tipos-vehiculo/{codigo}` (409 `TIPO_EN_USO` si tiene vehículos; 409
   `TIPO_INTEGRADO` para los del enunciado).
4. Si el tipo debe existir desde el arranque (precarga), añade una migración `V<n+1>__...sql` con el
   `INSERT` en `tipos_vehiculo`; nunca edites V1–V3.

## Steps — modelo de cobro nuevo (código)

1. **Constante** — añade el valor a `backend/src/main/java/mx/neology/acceso/domain/ModeloCobro.java`.
2. **Estrategia** — crea en `domain/cobro/` una clase que implemente `EstrategiaCobro`: devuelve el
   modelo en `modelo()`, el `MomentoCobro` en `momento()`, el importe en
   `calcular(BigDecimal tarifaPorMinuto, long minutos)` (usa `Importes.porMinuto`) y, si el modelo
   restringe la tarifa, sobrescribe `validarTarifa`.
3. **Bean** — regístrala en `infrastructure/config/CobroConfig.java`; `CalculadoraCobro` la recoge
   sola y, si falta, el arranque falla con `IllegalStateException` nombrando el modelo.
4. **Pruebas** — amplía `CalculadoraCobroTest` con los casos del modelo nuevo.
5. **Frontend** — añade el valor a `ModeloCobro` en `frontend/src/app/core/modelos/modelos.ts` y su
   etiqueta en el formulario de tipos.

## Verify

```bash
grep -rnE "switch ?\(.*([Tt]ipo|[Mm]odelo)|== ?ModeloCobro\." backend/src/main/java/mx/neology/acceso/application backend/src/main/java/mx/neology/acceso/web; test $? -eq 1   # expect: código 1 = ningún if/switch por tipo o modelo → la línea sale con 0
(cd backend && ./mvnw -q -B -ntp verify)   # expect: exit 0
(cd frontend && npm run test:ci)           # expect: exit 0
```

## Do not

- No añadas `if`/`switch` sobre el tipo ni sobre `ModeloCobro` en casos de uso, controladores ni
  adaptadores: pregúntale a `CalculadoraCobro`.
- No escribas tarifas en código ni en `application.yml`: viven en `tipos_vehiculo`.
- No edites una migración ya confirmada ni añadas un `CHECK` de tipos: los tipos son datos.
