package mx.neology.acceso.domain.cobro;

import java.math.BigDecimal;
import java.util.Objects;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.TipoVehiculo;

/** Los no residentes pagan por minuto al salir. */
public final class CobroNoResidente implements EstrategiaCobro {

  private final BigDecimal tarifaPorMinuto;

  public CobroNoResidente(BigDecimal tarifaPorMinuto) {
    this.tarifaPorMinuto = Objects.requireNonNull(tarifaPorMinuto, "tarifaPorMinuto");
  }

  @Override
  public TipoVehiculo tipo() {
    return TipoVehiculo.NO_RESIDENTE;
  }

  @Override
  public MomentoCobro momento() {
    return MomentoCobro.AL_SALIR;
  }

  @Override
  public BigDecimal calcular(long minutos) {
    return Importes.porMinuto(tarifaPorMinuto, minutos);
  }
}
