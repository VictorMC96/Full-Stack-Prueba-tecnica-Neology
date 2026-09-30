---
name: verificar-backend
description: Ejecuta la puerta completa del backend Spring Boot (formato Spotless, pruebas, cobertura JaCoCo y pureza de capas). Úsala antes de dar por terminada cualquier tarea que toque backend/, cuando pidan "verifica el backend", "corre las pruebas de Java" o "pasa la puerta", y cuando falle ./mvnw verify.
---

# Verificar backend

## When to use

- Antes de marcar como hecha una tarea que modificó algo bajo `backend/`.
- Cuando `./mvnw verify` falla y hay que saber qué parte de la puerta se rompió.

## Steps

1. Formatea: `(cd backend && ./mvnw -q -B -ntp spotless:apply)`.
2. Ejecuta la puerta completa: `(cd backend && ./mvnw -q -B -ntp verify)`.
3. Comprueba la pureza de capas (desde la raíz del proyecto):
   `grep -rlE "org\.springframework|jakarta\.persistence" backend/src/main/java/mx/neology/acceso/domain backend/src/main/java/mx/neology/acceso/application; test $? -eq 1`
4. Si falla, identifica la causa por el mensaje:
   - `spotless:check` → vuelve al paso 1.
   - Una prueba → ejecútala sola: `(cd backend && ./mvnw -q -B -ntp test -Dtest=NombreDeLaClaseTest)`.
   - `jacoco:check` → faltan pruebas unitarias en `domain` o `application`; abre
     `backend/target/site/jacoco/index.html` y añade las que cubran las líneas rojas.
   - `Schema-validation` → una entidad JPA no coincide con la migración Flyway; corrige la entidad o
     añade una migración nueva, nunca edites una ya confirmada.
5. Para probar a mano: `(cd backend && ./mvnw spring-boot:run)` y abre
   `http://localhost:8080/swagger-ui.html`.

## Verify

```bash
(cd backend && ./mvnw -q -B -ntp verify)   # expect: exit 0
grep -rlE "org\.springframework|jakarta\.persistence" backend/src/main/java/mx/neology/acceso/domain backend/src/main/java/mx/neology/acceso/application; test $? -eq 1   # expect: código 1 = sin coincidencias → la línea sale con 0
```

## Do not

- No bajes el umbral de JaCoCo ni añadas exclusiones para que pase.
- No uses `-DskipTests` ni `-Dspotless.check.skip`.
- No invoques `mvn`: Maven no está instalado; usa siempre `./mvnw`.
