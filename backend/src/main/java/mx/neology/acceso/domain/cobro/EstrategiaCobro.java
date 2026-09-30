package mx.neology.acceso.domain.cobro;

import java.math.BigDecimal;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.TipoVehiculo;

/** Una estrategia por tipo de vehículo: cuánto y cuándo se cobra. */
public interface EstrategiaCobro {

  TipoVehiculo tipo();

  MomentoCobro momento();

  /** Importe con escala 2 para los minutos completos indicados. */
  BigDecimal calcular(long minutos);
}
