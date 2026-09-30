package mx.neology.acceso.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import mx.neology.acceso.domain.excepcion.PlacaInvalidaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class PlacaTest {

  @ParameterizedTest
  @CsvSource({"abc-123 d,ABC123D", "' abc 123 ',ABC123", "xyz,XYZ", "AB-CD-EF-GH-IJ,ABCDEFGHIJ"})
  void normalizaMayusculasEspaciosYGuiones(String cruda, String esperada) {
    assertThat(Placa.de(cruda).valor()).isEqualTo(esperada);
  }

  @ParameterizedTest
  @ValueSource(strings = {"AB", "ABCDEFGHIJK", "", "ABC<1>", "ÁBC123", "a b"})
  @NullSource
  void rechazaPlacasInvalidas(String cruda) {
    assertThatThrownBy(() -> Placa.de(cruda))
        .isInstanceOf(PlacaInvalidaException.class)
        .satisfies(
            ex -> assertThat(((PlacaInvalidaException) ex).codigo()).isEqualTo("VALIDACION"));
  }

  @Test
  void normalizarSinValidarAdmiteFiltrosParciales() {
    assertThat(Placa.normalizar(" re-s ")).isEqualTo("RES");
    assertThat(Placa.normalizar(null)).isEmpty();
    assertThat(Placa.de("abc123")).hasToString("ABC123");
  }
}
