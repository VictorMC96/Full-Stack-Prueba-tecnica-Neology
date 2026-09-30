package mx.neology.acceso.domain.cobro;

import java.math.BigDecimal;
import mx.neology.acceso.domain.ModeloCobro;
import mx.neology.acceso.domain.MomentoCobro;

/** Tarifa por minuto cobrada al registrar la salida. */
public final class PorMinutoAlSalir implements EstrategiaCobro {

  @Override
  public ModeloCobro modelo() {
    return ModeloCobro.POR_MINUTO_AL_SALIR;
  }

  @Override
  public MomentoCobro momento() {
    return MomentoCobro.AL_SALIR;
  }

  @Override
  public BigDecimal calcular(BigDecimal tarifaPorMinuto, long minutos) {
    return Importes.porMinuto(tarifaPorMinuto, minutos);
  }
}
