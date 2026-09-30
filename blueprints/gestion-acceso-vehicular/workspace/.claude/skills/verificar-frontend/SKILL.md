---
name: verificar-frontend
description: Ejecuta la puerta completa del frontend Angular (formato Prettier, pruebas Karma en ChromeHeadless y build de producción). Úsala antes de dar por terminada cualquier tarea que toque frontend/, cuando pidan "verifica el frontend", "corre las pruebas de Angular" o "pasa la puerta", y cuando Karma no encuentre Chrome.
---

# Verificar frontend

## When to use

- Antes de marcar como hecha una tarea que modificó algo bajo `frontend/`.
- Cuando `npm run test:ci` falla o Karma no arranca el navegador.

## Steps

1. Formatea: `(cd frontend && npm run format)`.
2. Comprueba el formato: `(cd frontend && npm run format:check)`.
3. Ejecuta las pruebas: `(cd frontend && npm run test:ci)`.
4. Compila: `(cd frontend && npm run build)`.
5. Si Karma no encuentra Chrome (`No binary for ChromeHeadless browser`), define la ruta y repite el
   paso 3:

   ```bash
   export CHROME_BIN="/c/Program Files/Google/Chrome/Application/chrome.exe"
   ```

6. Si una prueba se queda colgada o falla de forma intermitente, revisa que espere con
   `await fixture.whenStable()` y que no use `fakeAsync`/`tick`: la app es zoneless. Para
   temporizadores usa `jasmine.clock()`.
7. Para probar a mano: arranca el backend (`(cd backend && ./mvnw spring-boot:run)`) y luego
   `(cd frontend && npm start)`; abre `http://localhost:4200`.

## Verify

```bash
(cd frontend && npm run format:check)   # expect: exit 0
(cd frontend && npm run test:ci)        # expect: exit 0, 0 fallos, 0 omitidos
(cd frontend && npm run build)          # expect: exit 0
```

## Do not

- No uses `fdescribe`, `fit`, `xdescribe` ni `xit` para que la suite pase.
- No subas los `budgets` de `angular.json` para silenciar un error de tamaño.
- No ejecutes `ng test` sin `--no-watch`: se queda esperando y bloquea la construcción.
