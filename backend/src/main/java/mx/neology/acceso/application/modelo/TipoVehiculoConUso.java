package mx.neology.acceso.application.modelo;

import mx.neology.acceso.domain.TipoVehiculo;

/** Tipo de vehículo con el número de vehículos registrados con él. */
public record TipoVehiculoConUso(TipoVehiculo tipo, long vehiculos) {}
