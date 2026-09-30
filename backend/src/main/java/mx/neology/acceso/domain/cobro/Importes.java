package mx.neology.acceso.domain.cobro;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Aritmética de importes compartida por las estrategias. */
final class Importes {

  static final BigDecimal CERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

  private Importes() {}

  static BigDecimal porMinuto(BigDecimal tarifa, long minutos) {
    if (minutos < 0) {
      throw new IllegalArgumentException("Los minutos no pueden ser negativos: " + minutos);
    }
    return tarifa.multiply(BigDecimal.valueOf(minutos)).setScale(2, RoundingMode.HALF_UP);
  }
}
