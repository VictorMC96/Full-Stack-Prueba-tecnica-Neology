package mx.neology.acceso.domain.cobro;

import java.math.BigDecimal;
import mx.neology.acceso.domain.ModeloCobro;
import mx.neology.acceso.domain.MomentoCobro;

/** Tarifa por minuto acumulada por periodo y cobrada en el informe mensual. */
public final class PorMinutoAcumulado implements EstrategiaCobro {

  @Override
  public ModeloCobro modelo() {
    return ModeloCobro.POR_MINUTO_ACUMULADO;
  }

  @Override
  public MomentoCobro momento() {
    return MomentoCobro.INFORME_MENSUAL;
  }

  @Override
  public BigDecimal calcular(BigDecimal tarifaPorMinuto, long minutos) {
    return Importes.porMinuto(tarifaPorMinuto, minutos);
  }
}
