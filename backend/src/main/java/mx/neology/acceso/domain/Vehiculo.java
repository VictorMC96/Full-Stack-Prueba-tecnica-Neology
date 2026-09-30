package mx.neology.acceso.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Vehículo registrado; {@code estanciaAbiertaId} apunta a la estancia en curso, si la hay. */
public record Vehiculo(
    UUID id,
    Placa placa,
    TipoVehiculo tipo,
    UUID estanciaAbiertaId,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {

  public static Vehiculo nuevo(Placa placa, TipoVehiculo tipo, OffsetDateTime ahora) {
    return new Vehiculo(UUID.randomUUID(), placa, tipo, null, ahora, ahora);
  }

  public boolean dentro() {
    return estanciaAbiertaId != null;
  }

  /** Solo un vehículo con tipo provisional puede reclasificarse a un tipo definitivo. */
  public boolean puedeReclasificarseA(TipoVehiculo nuevoTipo) {
    return tipo.esProvisional() && !nuevoTipo.esProvisional();
  }

  public Vehiculo reclasificar(TipoVehiculo nuevoTipo, OffsetDateTime ahora) {
    return new Vehiculo(id, placa, nuevoTipo, estanciaAbiertaId, createdAt, ahora);
  }

  public Vehiculo conEstanciaAbierta(UUID estanciaId, OffsetDateTime ahora) {
    return new Vehiculo(id, placa, tipo, estanciaId, createdAt, ahora);
  }

  public Vehiculo sinEstanciaAbierta(OffsetDateTime ahora) {
    return new Vehiculo(id, placa, tipo, null, createdAt, ahora);
  }
}
