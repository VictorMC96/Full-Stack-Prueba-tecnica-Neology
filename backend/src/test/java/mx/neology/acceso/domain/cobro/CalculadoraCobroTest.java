package mx.neology.acceso.domain.cobro;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import mx.neology.acceso.domain.CodigoTipo;
import mx.neology.acceso.domain.ModeloCobro;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.excepcion.ValidacionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CalculadoraCobroTest {

  private static final OffsetDateTime AHORA = OffsetDateTime.parse("2026-09-15T10:00:00-06:00");

  private final CalculadoraCobro calculadora =
      new CalculadoraCobro(
          List.of(new SinCobro(), new PorMinutoAlSalir(), new PorMinutoAcumulado()));

  private static TipoVehiculo tipo(ModeloCobro modelo, String tarifa) {
    return new TipoVehiculo(
        new CodigoTipo("TIPO"), "Tipo", modelo, new BigDecimal(tarifa), false, false, AHORA, AHORA);
  }

  @ParameterizedTest
  @CsvSource({
    "SIN_COBRO,0.00,11,0.00",
    "POR_MINUTO_ACUMULADO,0.05,11,0.55",
    "POR_MINUTO_AL_SALIR,0.50,11,5.50",
    "POR_MINUTO_AL_SALIR,0.30,3,0.90",
    "SIN_COBRO,0.00,0,0.00",
    "POR_MINUTO_ACUMULADO,0.05,0,0.00",
    "POR_MINUTO_AL_SALIR,0.50,0,0.00",
    "POR_MINUTO_ACUMULADO,0.05,1,0.05",
    "POR_MINUTO_AL_SALIR,0.50,3,1.50"
  })
  void calculaElImporteConLaTarifaDelTipoYEscalaDos(
      ModeloCobro modelo, String tarifa, long minutos, String esperado) {
    BigDecimal importe = calculadora.calcular(tipo(modelo, tarifa), minutos);
    assertThat(importe).isEqualByComparingTo(esperado);
    assertThat(importe.scale()).isEqualTo(2);
  }

  @ParameterizedTest
  @CsvSource({
    "SIN_COBRO,NINGUNO",
    "POR_MINUTO_ACUMULADO,INFORME_MENSUAL",
    "POR_MINUTO_AL_SALIR,AL_SALIR"
  })
  void conoceElMomentoDeCobroDeCadaModelo(ModeloCobro modelo, MomentoCobro momento) {
    assertThat(calculadora.momento(modelo)).isEqualTo(momento);
    assertThat(calculadora.momento(tipo(modelo, "0.00"))).isEqualTo(momento);
  }

  @Test
  void agrupaModelosPorMomento() {
    assertThat(calculadora.modelosConMomento(MomentoCobro.INFORME_MENSUAL))
        .containsExactly(ModeloCobro.POR_MINUTO_ACUMULADO);
    assertThat(calculadora.modelosConMomento(MomentoCobro.AL_SALIR))
        .containsExactly(ModeloCobro.POR_MINUTO_AL_SALIR);
    assertThat(calculadora.modelosConMomento(MomentoCobro.NINGUNO))
        .containsExactly(ModeloCobro.SIN_COBRO);
  }

  @Test
  void validaLaTarifaSegunElModelo() {
    assertThatCode(() -> calculadora.validarTarifa(ModeloCobro.SIN_COBRO, new BigDecimal("0.00")))
        .doesNotThrowAnyException();
    assertThatCode(
            () ->
                calculadora.validarTarifa(ModeloCobro.POR_MINUTO_AL_SALIR, new BigDecimal("0.30")))
        .doesNotThrowAnyException();
    assertThatThrownBy(
            () -> calculadora.validarTarifa(ModeloCobro.SIN_COBRO, new BigDecimal("0.10")))
        .isInstanceOf(ValidacionException.class);
    assertThatThrownBy(() -> calculadora.validarTarifa(ModeloCobro.SIN_COBRO, null))
        .isInstanceOf(ValidacionException.class);
    assertThatThrownBy(
            () -> calculadora.validarTarifa(ModeloCobro.POR_MINUTO_ACUMULADO, new BigDecimal("-1")))
        .isInstanceOf(ValidacionException.class);
    assertThatThrownBy(() -> calculadora.validarTarifa(ModeloCobro.POR_MINUTO_ACUMULADO, null))
        .isInstanceOf(ValidacionException.class);
  }

  @Test
  void faltaUnaEstrategiaLanzaIllegalState() {
    assertThatThrownBy(() -> new CalculadoraCobro(List.of(new SinCobro(), new PorMinutoAlSalir())))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("POR_MINUTO_ACUMULADO");
    assertThatThrownBy(() -> calculadora.validarTarifa(null, BigDecimal.ZERO))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void rechazaMinutosNegativos() {
    assertThatThrownBy(
            () -> calculadora.calcular(tipo(ModeloCobro.POR_MINUTO_AL_SALIR, "0.50"), -1))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
