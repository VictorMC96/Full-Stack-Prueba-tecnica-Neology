package mx.neology.acceso.domain;

import java.util.Arrays;

/** Tipos de vehículo. El tipo provisional es el que recibe una placa que entra sin alta previa. */
public enum TipoVehiculo {
  OFICIAL(false),
  RESIDENTE(false),
  NO_RESIDENTE(true);

  private final boolean provisional;

  TipoVehiculo(boolean provisional) {
    this.provisional = provisional;
  }

  /** Un tipo provisional puede reclasificarse después mediante un alta explícita. */
  public boolean esProvisional() {
    return provisional;
  }

  /** Tipo asignado a una placa desconocida que registra entrada. */
  public static TipoVehiculo porDefecto() {
    return Arrays.stream(values())
        .filter(TipoVehiculo::esProvisional)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No hay un tipo de vehículo provisional"));
  }
}
