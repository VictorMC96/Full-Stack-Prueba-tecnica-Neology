package mx.neology.acceso.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import mx.neology.acceso.application.modelo.FilaInforme;
import mx.neology.acceso.application.modelo.InformePagos;
import mx.neology.acceso.application.puerto.PeriodoRepositorio;
import mx.neology.acceso.application.puerto.TipoVehiculoRepositorio;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.CodigoTipo;
import mx.neology.acceso.domain.ModeloCobro;
import mx.neology.acceso.domain.Periodo;
import mx.neology.acceso.soporte.TransaccionDirecta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GenerarInformePagosTest {

  @Mock VehiculoRepositorio vehiculos;
  @Mock PeriodoRepositorio periodos;
  @Mock TipoVehiculoRepositorio tipos;

  private GenerarInformePagos casoDeUso() {
    return new GenerarInformePagos(
        vehiculos, periodos, tipos, Fixtures.calculadora(), new TransaccionDirecta());
  }

  @Test
  void incluyeSoloLosTiposQueCobranEnElInformeYOrdenaPorPlaca() {
    Periodo periodo = Fixtures.periodoActivo();
    when(periodos.activo()).thenReturn(periodo);
    when(tipos.listar())
        .thenReturn(
            List.of(
                Fixtures.NO_RESIDENTE,
                Fixtures.OFICIAL,
                Fixtures.RESIDENTE,
                Fixtures.tipo(
                    "PENSION", "Pensión", ModeloCobro.POR_MINUTO_ACUMULADO, "0.02", false, false)));
    when(vehiculos.acumuladosPorTipo(
            Set.of(new CodigoTipo("RESIDENTE"), new CodigoTipo("PENSION")), periodo.id()))
        .thenReturn(
            List.of(
                new FilaInforme("RES003", 60, new BigDecimal("3.00")),
                new FilaInforme("PEN001", 0, null),
                new FilaInforme("RES001", 210, new BigDecimal("10.5"))));

    InformePagos informe = casoDeUso().ejecutar();

    assertThat(informe.periodo()).isEqualTo(periodo);
    assertThat(informe.filas())
        .extracting(FilaInforme::placa)
        .containsExactly("PEN001", "RES001", "RES003");
    assertThat(informe.filas().get(0).importe()).isEqualByComparingTo("0.00");
    assertThat(informe.filas().get(1).importe().scale()).isEqualTo(2);
    assertThat(informe.totalMinutos()).isEqualTo(270);
    assertThat(informe.totalImporte()).isEqualByComparingTo("13.50");
    assertThat(informe.totalImporte().scale()).isEqualTo(2);
  }

  @Test
  void sinResidentesDevuelveInformeVacio() {
    Periodo periodo = Fixtures.periodoActivo();
    when(periodos.activo()).thenReturn(periodo);
    when(tipos.listar()).thenReturn(List.of(Fixtures.OFICIAL, Fixtures.NO_RESIDENTE));
    when(vehiculos.acumuladosPorTipo(Set.of(), periodo.id())).thenReturn(List.of());

    InformePagos informe = casoDeUso().ejecutar();

    assertThat(informe.filas()).isEmpty();
    assertThat(informe.totalMinutos()).isZero();
    assertThat(informe.totalImporte()).isEqualByComparingTo("0.00");
  }
}
