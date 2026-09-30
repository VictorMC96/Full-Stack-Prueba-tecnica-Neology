package mx.neology.acceso.application.modelo;

import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.Vehiculo;

/** Vehículo junto con su tipo del catálogo. */
public record VehiculoConTipo(Vehiculo vehiculo, TipoVehiculo tipo) {}
