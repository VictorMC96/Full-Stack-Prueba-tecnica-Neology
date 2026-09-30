package mx.neology.acceso.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EstanciaTest {

  private static final OffsetDateTime ENTRADA = OffsetDateTime.parse("2026-09-15T10:00:00-06:00");

  @ParameterizedTest
  @CsvSource({"0,0", "59,0", "605,10", "60,1", "3600,60"})
  void cuentaSoloMinutosCompletos(long segundos, long esperados) {
    assertThat(Estancia.minutosCompletos(ENTRADA, ENTRADA.plusSeconds(segundos)))
        .isEqualTo(esperados);
  }

  @Test
  void abrirYCerrarConservanIdYEntrada() {
    UUID vehiculo = UUID.randomUUID();
    Estancia abierta = Estancia.abrir(vehiculo, ENTRADA);
    assertThat(abierta.abierta()).isTrue();
    assertThat(abierta.vehiculoId()).isEqualTo(vehiculo);
    UUID periodo = UUID.randomUUID();
    Estancia cerrada = abierta.cerrar(ENTRADA.plusMinutes(11), 11, new BigDecimal("0.55"), periodo);
    assertThat(cerrada.abierta()).isFalse();
    assertThat(cerrada.id()).isEqualTo(abierta.id());
    assertThat(cerrada.minutos()).isEqualTo(11);
    assertThat(cerrada.importe()).isEqualByComparingTo("0.55");
    assertThat(cerrada.periodoId()).isEqualTo(periodo);
    assertThat(cerrada.updatedAt()).isEqualTo(ENTRADA.plusMinutes(11));
  }

  @Test
  void modelosDeVehiculoPeriodoYAcumulado() {
    Vehiculo v = Vehiculo.nuevo(Placa.de("abc123"), TipoVehiculo.porDefecto(), ENTRADA);
    assertThat(v.tipo()).isEqualTo(TipoVehiculo.NO_RESIDENTE);
    assertThat(v.dentro()).isFalse();
    assertThat(v.puedeReclasificarseA(TipoVehiculo.RESIDENTE)).isTrue();
    assertThat(v.puedeReclasificarseA(TipoVehiculo.NO_RESIDENTE)).isFalse();
    Vehiculo residente = v.reclasificar(TipoVehiculo.RESIDENTE, ENTRADA);
    assertThat(residente.id()).isEqualTo(v.id());
    assertThat(residente.puedeReclasificarseA(TipoVehiculo.OFICIAL)).isFalse();
    UUID estancia = UUID.randomUUID();
    assertThat(v.conEstanciaAbierta(estancia, ENTRADA).dentro()).isTrue();
    assertThat(v.conEstanciaAbierta(estancia, ENTRADA).sinEstanciaAbierta(ENTRADA).dentro())
        .isFalse();

    Periodo periodo = Periodo.abrir(ENTRADA);
    assertThat(periodo.esActivo()).isTrue();
    Periodo cerrado = periodo.cerrar(ENTRADA.plusDays(1));
    assertThat(cerrado.esActivo()).isFalse();
    assertThat(cerrado.activo()).isNull();
    assertThat(cerrado.fin()).isEqualTo(ENTRADA.plusDays(1));

    Acumulado acumulado = Acumulado.nuevo(v.id(), periodo.id(), ENTRADA);
    assertThat(acumulado.minutos()).isZero();
    assertThat(acumulado.importe()).isEqualByComparingTo("0.00");
    Acumulado sumado =
        acumulado
            .sumar(11, new BigDecimal("0.55"), ENTRADA)
            .sumar(9, new BigDecimal("0.45"), ENTRADA);
    assertThat(sumado.minutos()).isEqualTo(20);
    assertThat(sumado.importe()).isEqualByComparingTo("1.00");
    assertThat(sumado.importe().scale()).isEqualTo(2);
  }
}
