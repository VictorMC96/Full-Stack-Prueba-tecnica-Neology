# Entrega: fork, push y pull request

Pasos manuales (no los ejecuta ningún agente):

1. Crear el fork de `VictorMC96/Full-Stack-Prueba-tecnica-Neology` en la cuenta `xochitlgr` (botón
   *Fork* en GitHub).
2. Conectar el fork y subir la rama:

   ```bash
   git remote add origin https://github.com/xochitlgr/Full-Stack-Prueba-tecnica-Neology.git
   git push -u origin feature/xochitlgr
   ```

3. Abrir el pull request `xochitlgr:feature/xochitlgr` → `VictorMC96:main` con el texto de abajo.
4. Enviar el enlace del PR al correo de la consultora con copia a `vmiranda@neology.mx` y
   `lluna@neopartners.mx`.

## Título

`Prueba técnica Full Stack – Xochitl (feature/xochitlgr)`

## Descripción

Sistema de gestión de acceso vehicular con Angular 22 + Angular Material y Spring Boot 4.1.1 + JPA + H2.

- Backend con Clean Architecture (`domain` / `application` / `infrastructure` / `web`), Strategy para
  el cobro por tipo de vehículo, Flyway con base precargada, errores RFC 7807, Swagger.
- Frontend con listado (filtro, paginación, ordenamiento), detalle por vehículo, formularios de
  operación (entrada, salida, alta, iniciar mes) y reporte de residentes con descarga en Excel.
- Pruebas: JUnit 5 + Mockito + AssertJ con JaCoCo en backend; Jasmine + Karma en frontend; CI en
  GitHub Actions.
- Instrucciones completas en el README; decisiones de diseño en `docs/decisiones.md`; capturas en
  `docs/capturas/`.
