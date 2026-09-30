package mx.neology.acceso.domain.cobro;

import java.math.BigDecimal;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.TipoVehiculo;

/** Los vehículos oficiales no pagan. */
public final class CobroOficial implements EstrategiaCobro {

  @Override
  public TipoVehiculo tipo() {
    return TipoVehiculo.OFICIAL;
  }

  @Override
  public MomentoCobro momento() {
    return MomentoCobro.NINGUNO;
  }

  @Override
  public BigDecimal calcular(long minutos) {
    return Importes.CERO;
  }
}
