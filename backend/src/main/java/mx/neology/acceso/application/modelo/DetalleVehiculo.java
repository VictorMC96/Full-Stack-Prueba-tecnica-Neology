package mx.neology.acceso.application.modelo;

import java.util.Optional;
import mx.neology.acceso.domain.Acumulado;
import mx.neology.acceso.domain.Vehiculo;

/** Vehículo con su estancia abierta y su acumulado del periodo activo, cuando aplican. */
public record DetalleVehiculo(
    Vehiculo vehiculo,
    Optional<EstanciaDeVehiculo> estanciaAbierta,
    Optional<Acumulado> acumuladoPeriodo) {}
