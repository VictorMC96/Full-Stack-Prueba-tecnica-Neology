package mx.neology.acceso.web;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import mx.neology.acceso.domain.MomentoCobro;

/**
 * Estancia de un vehículo. {@code duracionSegundos} es el tiempo real entre entrada y salida;
 * {@code minutos} son los minutos cobrados (todo minuto iniciado cuenta completo). Ambos son null
 * mientras la estancia sigue abierta. {@code tipo} es el código del tipo del vehículo.
 */
public record EstanciaResponse(
    UUID id,
    String placa,
    String tipo,
    String tipoNombre,
    OffsetDateTime entrada,
    OffsetDateTime salida,
    Long duracionSegundos,
    Integer minutos,
    BigDecimal importe,
    MomentoCobro momentoCobro,
    UUID periodoId) {}
