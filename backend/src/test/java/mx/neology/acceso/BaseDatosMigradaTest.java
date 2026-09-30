package mx.neology.acceso;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import mx.neology.acceso.soporte.IntegracionBase;
import org.junit.jupiter.api.Test;

class BaseDatosMigradaTest extends IntegracionBase {

  @Test
  void flywayAplicoLasMigracionesYLaPrecargaCuadra() {
    Integer migraciones =
        jdbc.queryForObject(
            "select count(*) from \"flyway_schema_history\" where \"success\" = true and"
                + " \"version\" is not null",
            Integer.class);
    assertThat(migraciones).isEqualTo(3);
    assertThat(contar("vehiculos")).isEqualTo(6);
    assertThat(contar("estancias")).isEqualTo(7);
    assertThat(contar("periodos")).isEqualTo(1);
    assertThat(contar("acumulados_residentes")).isEqualTo(3);
    assertThat(contar("tipos_vehiculo")).isEqualTo(3);
    BigDecimal suma =
        jdbc.queryForObject("select sum(importe) from acumulados_residentes", BigDecimal.class);
    assertThat(suma).isEqualByComparingTo("15.00");
  }

  @Test
  void lasTarifasVivenEnElCatalogoDeTipos() {
    assertThat(
            jdbc.queryForObject(
                "select tarifa_por_minuto from tipos_vehiculo where codigo = 'RESIDENTE'",
                BigDecimal.class))
        .isEqualByComparingTo("0.05");
    assertThat(
            jdbc.queryForObject(
                "select tarifa_por_minuto from tipos_vehiculo where codigo = 'NO_RESIDENTE'",
                BigDecimal.class))
        .isEqualByComparingTo("0.50");
    Integer provisionales =
        jdbc.queryForObject(
            "select count(*) from tipos_vehiculo where provisional = true", Integer.class);
    assertThat(provisionales).isEqualTo(1);
  }
}
