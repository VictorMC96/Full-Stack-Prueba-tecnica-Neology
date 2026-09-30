package mx.neology.acceso.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Map;
import mx.neology.acceso.domain.TipoVehiculo;
import org.junit.jupiter.api.Test;

class TarifasPropertiesTest {

  @Test
  void resuelveLaClaveDelTipoEnMinusculasYConGuiones() {
    TarifasProperties tarifas =
        new TarifasProperties(
            Map.of("residente", new BigDecimal("0.05"), "no-residente", new BigDecimal("0.50")));

    assertThat(TarifasProperties.clave(TipoVehiculo.NO_RESIDENTE)).isEqualTo("no-residente");
    assertThat(tarifas.porMinuto(TipoVehiculo.NO_RESIDENTE)).isEqualByComparingTo("0.50");
  }

  @Test
  void faltaDeTarifaFallaNombrandoLaClave() {
    TarifasProperties tarifas = new TarifasProperties(Map.of("residente", new BigDecimal("0.05")));

    assertThatThrownBy(() -> tarifas.porMinuto(TipoVehiculo.NO_RESIDENTE))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("acceso.tarifas.no-residente");
  }
}
