package mx.neology.acceso.domain.cobro;

import java.math.BigDecimal;
import mx.neology.acceso.domain.ModeloCobro;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.excepcion.ValidacionException;

/** Una estrategia por modelo de cobro: cuánto se cobra, cuándo, y qué tarifa admite. */
public interface EstrategiaCobro {

  ModeloCobro modelo();

  MomentoCobro momento();

  /** Importe con escala 2 para la tarifa del tipo y los minutos indicados. */
  BigDecimal calcular(BigDecimal tarifaPorMinuto, long minutos);

  /** Valida la tarifa que un tipo puede configurar para este modelo. */
  default void validarTarifa(BigDecimal tarifaPorMinuto) {
    if (tarifaPorMinuto == null || tarifaPorMinuto.signum() < 0) {
      throw new ValidacionException(
          "tarifaPorMinuto", "La tarifa por minuto debe ser mayor o igual que 0.00");
    }
  }
}
