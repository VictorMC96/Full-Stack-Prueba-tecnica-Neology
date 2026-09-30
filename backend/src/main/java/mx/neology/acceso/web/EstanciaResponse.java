package mx.neology.acceso.web;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.TipoVehiculo;

public record EstanciaResponse(
    UUID id,
    String placa,
    TipoVehiculo tipo,
    OffsetDateTime entrada,
    OffsetDateTime salida,
    Integer minutos,
    BigDecimal importe,
    MomentoCobro momentoCobro,
    UUID periodoId) {}
