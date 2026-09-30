package mx.neology.acceso.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Periodo de facturación; {@code activo} es {@code TRUE} o {@code null}, nunca {@code FALSE}. */
public record Periodo(
    UUID id,
    OffsetDateTime inicio,
    OffsetDateTime fin,
    Boolean activo,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {

  public static Periodo abrir(OffsetDateTime ahora) {
    return new Periodo(UUID.randomUUID(), ahora, null, Boolean.TRUE, ahora, ahora);
  }

  public boolean esActivo() {
    return Boolean.TRUE.equals(activo);
  }

  public Periodo cerrar(OffsetDateTime ahora) {
    return new Periodo(id, inicio, ahora, null, createdAt, ahora);
  }
}
