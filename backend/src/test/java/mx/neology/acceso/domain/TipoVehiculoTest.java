package mx.neology.acceso.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import mx.neology.acceso.domain.excepcion.ValidacionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class TipoVehiculoTest {

  private static final OffsetDateTime AHORA = OffsetDateTime.parse("2026-09-15T10:00:00-06:00");

  @ParameterizedTest
  @CsvSource({" visitante ,VISITANTE", "no_residente,NO_RESIDENTE", "ab1,AB1"})
  void elCodigoSeNormaliza(String crudo, String esperado) {
    assertThat(CodigoTipo.de(crudo).valor()).isEqualTo(esperado);
    assertThat(CodigoTipo.de(crudo)).hasToString(esperado);
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB", "1ABC", "ABC-DEF", "ABCDEFGHIJKLMNOPQRSTU", "", "con espacio"})
  @NullSource
  void elCodigoInvalidoEsUnErrorDeValidacion(String crudo) {
    assertThatThrownBy(() -> CodigoTipo.de(crudo))
        .isInstanceOf(ValidacionException.class)
        .satisfies(ex -> assertThat(((ValidacionException) ex).campo()).isEqualTo("codigo"));
  }

  @Test
  void unTipoNuevoNormalizaNombreYTarifa() {
    TipoVehiculo tipo =
        TipoVehiculo.nuevo(
            CodigoTipo.de("VISITANTE"),
            "  Visitante ",
            ModeloCobro.POR_MINUTO_AL_SALIR,
            new BigDecimal("0.3"),
            AHORA);
    assertThat(tipo.nombre()).isEqualTo("Visitante");
    assertThat(tipo.tarifaPorMinuto()).isEqualTo(new BigDecimal("0.30"));
    assertThat(tipo.provisional()).isFalse();
    assertThat(tipo.integrado()).isFalse();
    TipoVehiculo actualizado =
        tipo.actualizar(
            "Visita", ModeloCobro.POR_MINUTO_ACUMULADO, BigDecimal.ONE, AHORA.plusDays(1));
    assertThat(actualizado.codigo()).isEqualTo(tipo.codigo());
    assertThat(actualizado.updatedAt()).isEqualTo(AHORA.plusDays(1));
    assertThat(actualizado.createdAt()).isEqualTo(AHORA);
  }

  @Test
  void validaNombreModeloYTarifa() {
    CodigoTipo codigo = CodigoTipo.de("NUEVO");
    assertThatThrownBy(
            () -> TipoVehiculo.nuevo(codigo, "X", ModeloCobro.SIN_COBRO, BigDecimal.ZERO, AHORA))
        .isInstanceOf(ValidacionException.class);
    assertThatThrownBy(
            () ->
                TipoVehiculo.nuevo(
                    codigo, "A".repeat(41), ModeloCobro.SIN_COBRO, BigDecimal.ZERO, AHORA))
        .isInstanceOf(ValidacionException.class);
    assertThatThrownBy(() -> TipoVehiculo.nuevo(codigo, "Nuevo", null, BigDecimal.ZERO, AHORA))
        .isInstanceOf(ValidacionException.class);
    assertThatThrownBy(
            () -> TipoVehiculo.nuevo(codigo, "Nuevo", ModeloCobro.SIN_COBRO, null, AHORA))
        .isInstanceOf(ValidacionException.class);
    assertThatThrownBy(
            () ->
                TipoVehiculo.nuevo(
                    codigo, "Nuevo", ModeloCobro.SIN_COBRO, new BigDecimal("-0.01"), AHORA))
        .isInstanceOf(ValidacionException.class);
    assertThatThrownBy(
            () ->
                TipoVehiculo.nuevo(
                    codigo, "Nuevo", ModeloCobro.SIN_COBRO, new BigDecimal("0.001"), AHORA))
        .isInstanceOf(ValidacionException.class);
  }

  @Test
  void soloElTipoProvisionalPermiteReclasificar() {
    TipoVehiculo provisional =
        new TipoVehiculo(
            CodigoTipo.de("NO_RESIDENTE"),
            "No residente",
            ModeloCobro.POR_MINUTO_AL_SALIR,
            new BigDecimal("0.50"),
            true,
            true,
            AHORA,
            AHORA);
    TipoVehiculo definitivo =
        TipoVehiculo.nuevo(
            CodigoTipo.de("VISITANTE"),
            "Visitante",
            ModeloCobro.POR_MINUTO_AL_SALIR,
            new BigDecimal("0.30"),
            AHORA);
    assertThat(provisional.permiteReclasificarA(definitivo)).isTrue();
    assertThat(provisional.permiteReclasificarA(provisional)).isFalse();
    assertThat(definitivo.permiteReclasificarA(provisional)).isFalse();
    assertThat(definitivo.permiteReclasificarA(definitivo)).isFalse();
  }
}
