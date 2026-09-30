package mx.neology.acceso.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Minutos e importe acumulados por un vehículo en un periodo. */
public record Acumulado(
    UUID id,
    UUID vehiculoId,
    UUID periodoId,
    int minutos,
    BigDecimal importe,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {

  public static Acumulado nuevo(UUID vehiculoId, UUID periodoId, OffsetDateTime ahora) {
    return new Acumulado(
        UUID.randomUUID(),
        vehiculoId,
        periodoId,
        0,
        BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
        ahora,
        ahora);
  }

  public Acumulado sumar(long minutosExtra, BigDecimal importeExtra, OffsetDateTime ahora) {
    return new Acumulado(
        id,
        vehiculoId,
        periodoId,
        Math.toIntExact(minutos + minutosExtra),
        importe.add(importeExtra).setScale(2, RoundingMode.HALF_UP),
        createdAt,
        ahora);
  }
}
