package mx.neology.acceso.domain.cobro;

import java.math.BigDecimal;
import mx.neology.acceso.domain.ModeloCobro;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.excepcion.ValidacionException;

/** No se cobra nunca; la tarifa debe ser 0.00. */
public final class SinCobro implements EstrategiaCobro {

  @Override
  public ModeloCobro modelo() {
    return ModeloCobro.SIN_COBRO;
  }

  @Override
  public MomentoCobro momento() {
    return MomentoCobro.NINGUNO;
  }

  @Override
  public BigDecimal calcular(BigDecimal tarifaPorMinuto, long minutos) {
    return Importes.CERO;
  }

  @Override
  public void validarTarifa(BigDecimal tarifaPorMinuto) {
    if (tarifaPorMinuto == null || tarifaPorMinuto.compareTo(BigDecimal.ZERO) != 0) {
      throw new ValidacionException(
          "tarifaPorMinuto", "Un tipo sin cobro debe tener tarifa por minuto 0.00");
    }
  }
}
