package mx.neology.acceso.web;

import java.time.OffsetDateTime;
import java.util.UUID;

public record VehiculoDetalleResponse(
    UUID id,
    String placa,
    String tipo,
    String tipoNombre,
    boolean dentro,
    EstanciaResponse estanciaAbierta,
    AcumuladoResponse acumuladoPeriodo,
    OffsetDateTime createdAt) {}
