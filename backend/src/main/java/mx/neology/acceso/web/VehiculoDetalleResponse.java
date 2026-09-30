package mx.neology.acceso.web;

import java.time.OffsetDateTime;
import java.util.UUID;
import mx.neology.acceso.domain.TipoVehiculo;

public record VehiculoDetalleResponse(
    UUID id,
    String placa,
    TipoVehiculo tipo,
    boolean dentro,
    EstanciaResponse estanciaAbierta,
    AcumuladoResponse acumuladoPeriodo,
    OffsetDateTime createdAt) {}
