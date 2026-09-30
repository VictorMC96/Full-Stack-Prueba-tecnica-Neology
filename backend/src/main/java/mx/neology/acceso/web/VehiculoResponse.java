package mx.neology.acceso.web;

import java.time.OffsetDateTime;
import java.util.UUID;

public record VehiculoResponse(
    UUID id,
    String placa,
    String tipo,
    String tipoNombre,
    boolean dentro,
    OffsetDateTime createdAt) {}
