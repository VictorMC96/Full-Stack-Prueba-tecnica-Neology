package mx.neology.acceso.web;

import java.time.OffsetDateTime;
import mx.neology.acceso.domain.TipoVehiculo;

public record VehiculoResumen(
    String placa, TipoVehiculo tipo, boolean dentro, OffsetDateTime createdAt) {}
