package mx.neology.acceso.application.modelo;

import mx.neology.acceso.domain.Estancia;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.Placa;
import mx.neology.acceso.domain.TipoVehiculo;

/** Estancia junto con los datos del vehículo que la capa web necesita para responder. */
public record EstanciaDeVehiculo(
    Estancia estancia, Placa placa, TipoVehiculo tipo, MomentoCobro momentoCobro) {}
