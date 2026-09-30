package mx.neology.acceso.web;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.TipoVehiculo;

/**
 * Estancia de un vehículo. {@code duracionSegundos} es el tiempo real entre entrada y salida;
 * {@code minutos} son los minutos cobrados (todo minuto iniciado cuenta completo). Ambos son null
 * mientras la estancia sigue abierta.
 */
public record EstanciaResponse(
    UUID id,
    String placa,
    TipoVehiculo tipo,
    OffsetDateTime entrada,
    OffsetDateTime salida,
    Long duracionSegundos,
    Integer minutos,
    BigDecimal importe,
    MomentoCobro momentoCobro,
    UUID periodoId) {}
