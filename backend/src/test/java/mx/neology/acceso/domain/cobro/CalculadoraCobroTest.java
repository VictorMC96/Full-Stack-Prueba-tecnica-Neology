package mx.neology.acceso.domain.cobro;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.TipoVehiculo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CalculadoraCobroTest {

  private final CalculadoraCobro calculadora =
      new CalculadoraCobro(
          List.of(
              new CobroOficial(),
              new CobroResidente(new BigDecimal("0.05")),
              new CobroNoResidente(new BigDecimal("0.50"))));

  @ParameterizedTest
  @CsvSource({
    "OFICIAL,11,0.00",
    "RESIDENTE,11,0.55",
    "NO_RESIDENTE,11,5.50",
    "OFICIAL,0,0.00",
    "RESIDENTE,0,0.00",
    "NO_RESIDENTE,0,0.00",
    "RESIDENTE,1,0.05",
    "NO_RESIDENTE,3,1.50"
  })
  void calculaElImporteConEscalaDos(TipoVehiculo tipo, long minutos, String esperado) {
    BigDecimal importe = calculadora.calcular(tipo, minutos);
    assertThat(importe).isEqualByComparingTo(esperado);
    assertThat(importe.scale()).isEqualTo(2);
  }

  @ParameterizedTest
  @CsvSource({"OFICIAL,NINGUNO", "RESIDENTE,INFORME_MENSUAL", "NO_RESIDENTE,AL_SALIR"})
  void conoceElMomentoDeCobro(TipoVehiculo tipo, MomentoCobro momento) {
    assertThat(calculadora.momento(tipo)).isEqualTo(momento);
  }

  @Test
  void agrupaTiposPorMomento() {
    assertThat(calculadora.tiposConMomento(MomentoCobro.INFORME_MENSUAL))
        .containsExactly(TipoVehiculo.RESIDENTE);
    assertThat(calculadora.tiposConMomento(MomentoCobro.AL_SALIR))
        .containsExactly(TipoVehiculo.NO_RESIDENTE);
  }

  @Test
  void faltaUnaEstrategiaLanzaIllegalState() {
    assertThatThrownBy(
            () ->
                new CalculadoraCobro(
                    List.of(new CobroOficial(), new CobroResidente(new BigDecimal("0.05")))))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("NO_RESIDENTE");
  }

  @Test
  void rechazaMinutosNegativos() {
    assertThatThrownBy(() -> calculadora.calcular(TipoVehiculo.RESIDENTE, -1))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
