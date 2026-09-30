package mx.neology.acceso.domain;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Estancia de un vehículo; abierta mientras {@code salida} es nulo. */
public record Estancia(
    UUID id,
    UUID vehiculoId,
    OffsetDateTime entrada,
    OffsetDateTime salida,
    Integer minutos,
    BigDecimal importe,
    UUID periodoId,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {

  /** Minutos completos entre entrada y salida; los segundos sobrantes se descartan. */
  public static long minutosCompletos(OffsetDateTime entrada, OffsetDateTime salida) {
    return Duration.between(entrada, salida).toMinutes();
  }

  public static Estancia abrir(UUID vehiculoId, OffsetDateTime ahora) {
    return new Estancia(UUID.randomUUID(), vehiculoId, ahora, null, null, null, null, ahora, ahora);
  }

  public boolean abierta() {
    return salida == null;
  }

  public Estancia cerrar(
      OffsetDateTime momento, long minutosCobrados, BigDecimal cobro, UUID periodo) {
    return new Estancia(
        id,
        vehiculoId,
        entrada,
        momento,
        Math.toIntExact(minutosCobrados),
        cobro,
        periodo,
        createdAt,
        momento);
  }
}
