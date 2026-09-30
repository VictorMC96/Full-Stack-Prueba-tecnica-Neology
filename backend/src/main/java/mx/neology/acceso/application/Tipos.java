package mx.neology.acceso.application;

import mx.neology.acceso.application.puerto.TipoVehiculoRepositorio;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.Vehiculo;

/** Resolución del tipo de un vehículo ya registrado (siempre existe por la clave foránea). */
final class Tipos {

  private Tipos() {}

  static TipoVehiculo de(TipoVehiculoRepositorio tipos, Vehiculo vehiculo) {
    return tipos
        .buscar(vehiculo.tipo())
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "El vehículo "
                        + vehiculo.placa()
                        + " tiene el tipo desconocido "
                        + vehiculo.tipo()));
  }
}
