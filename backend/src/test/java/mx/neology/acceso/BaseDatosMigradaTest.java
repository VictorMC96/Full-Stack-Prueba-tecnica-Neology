package mx.neology.acceso;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import mx.neology.acceso.infrastructure.config.TarifasProperties;
import mx.neology.acceso.soporte.IntegracionBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BaseDatosMigradaTest extends IntegracionBase {

  @Autowired TarifasProperties tarifas;

  @Test
  void flywayAplicoLasDosMigracionesYLaPrecargaCuadra() {
    Integer migraciones =
        jdbc.queryForObject(
            "select count(*) from \"flyway_schema_history\" where \"success\" = true and"
                + " \"version\" is not null",
            Integer.class);
    assertThat(migraciones).isEqualTo(2);
    assertThat(contar("vehiculos")).isEqualTo(6);
    assertThat(contar("estancias")).isEqualTo(7);
    assertThat(contar("periodos")).isEqualTo(1);
    assertThat(contar("acumulados_residentes")).isEqualTo(3);
    BigDecimal suma =
        jdbc.queryForObject("select sum(importe) from acumulados_residentes", BigDecimal.class);
    assertThat(suma).isEqualByComparingTo("15.00");
  }

  @Test
  void lasTarifasSeLeenDeLaConfiguracion() {
    assertThat(tarifas.residente()).isEqualByComparingTo("0.05");
    assertThat(tarifas.noResidente()).isEqualByComparingTo("0.50");
  }
}
