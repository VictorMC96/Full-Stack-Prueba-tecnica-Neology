package mx.neology.acceso.domain.cobro;

import java.math.BigDecimal;
import java.util.Objects;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.TipoVehiculo;

/** Los residentes pagan por minuto, acumulado en el informe mensual. */
public final class CobroResidente implements EstrategiaCobro {

  private final BigDecimal tarifaPorMinuto;

  public CobroResidente(BigDecimal tarifaPorMinuto) {
    this.tarifaPorMinuto = Objects.requireNonNull(tarifaPorMinuto, "tarifaPorMinuto");
  }

  @Override
  public TipoVehiculo tipo() {
    return TipoVehiculo.RESIDENTE;
  }

  @Override
  public MomentoCobro momento() {
    return MomentoCobro.INFORME_MENSUAL;
  }

  @Override
  public BigDecimal calcular(long minutos) {
    return Importes.porMinuto(tarifaPorMinuto, minutos);
  }
}
