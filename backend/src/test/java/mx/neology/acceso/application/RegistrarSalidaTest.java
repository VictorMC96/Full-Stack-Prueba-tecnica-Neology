package mx.neology.acceso.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import mx.neology.acceso.application.modelo.EstanciaDeVehiculo;
import mx.neology.acceso.application.puerto.AcumuladoRepositorio;
import mx.neology.acceso.application.puerto.EstanciaRepositorio;
import mx.neology.acceso.application.puerto.PeriodoRepositorio;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.Acumulado;
import mx.neology.acceso.domain.Estancia;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.Periodo;
import mx.neology.acceso.domain.Placa;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.Vehiculo;
import mx.neology.acceso.domain.excepcion.VehiculoNoDentroException;
import mx.neology.acceso.domain.excepcion.VehiculoNoEncontradoException;
import mx.neology.acceso.soporte.TransaccionDirecta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistrarSalidaTest {

  @Mock VehiculoRepositorio vehiculos;
  @Mock EstanciaRepositorio estancias;
  @Mock PeriodoRepositorio periodos;
  @Mock AcumuladoRepositorio acumulados;
  RegistrarSalida casoDeUso;
  Periodo periodo = Fixtures.periodoActivo();

  @BeforeEach
  void setUp() {
    casoDeUso =
        new RegistrarSalida(
            vehiculos,
            estancias,
            periodos,
            acumulados,
            Fixtures.calculadora(),
            new TransaccionDirecta(),
            Fixtures.RELOJ);
  }

  private Vehiculo dentroDesdeHace(String placa, TipoVehiculo tipo, long segundos) {
    Vehiculo base = Fixtures.vehiculo(placa, tipo);
    Estancia abierta = Estancia.abrir(base.id(), Fixtures.AHORA.minusSeconds(segundos));
    Vehiculo dentro = base.conEstanciaAbierta(abierta.id(), abierta.entrada());
    when(vehiculos.buscarPorPlacaParaActualizar(dentro.placa())).thenReturn(Optional.of(dentro));
    when(estancias.buscarPorId(abierta.id())).thenReturn(Optional.of(abierta));
    when(estancias.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
    when(vehiculos.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
    when(periodos.activo()).thenReturn(periodo);
    return dentro;
  }

  @Test
  void noResidenteCobraAlSalirConMinutosTruncados() {
    dentroDesdeHace("NOR001", TipoVehiculo.NO_RESIDENTE, 10 * 60 + 5);

    EstanciaDeVehiculo resultado = casoDeUso.ejecutar("nor001");

    assertThat(resultado.estancia().minutos()).isEqualTo(10);
    assertThat(resultado.estancia().importe()).isEqualByComparingTo("5.00");
    assertThat(resultado.estancia().salida()).isEqualTo(Fixtures.AHORA);
    assertThat(resultado.estancia().periodoId()).isEqualTo(periodo.id());
    assertThat(resultado.momentoCobro()).isEqualTo(MomentoCobro.AL_SALIR);
    ArgumentCaptor<Vehiculo> actualizado = ArgumentCaptor.forClass(Vehiculo.class);
    verify(vehiculos).guardar(actualizado.capture());
    assertThat(actualizado.getValue().dentro()).isFalse();
    verify(acumulados, never()).guardar(any());
  }

  @Test
  void residenteAcumulaEnElPeriodoActivo() {
    Vehiculo residente = dentroDesdeHace("RES001", TipoVehiculo.RESIDENTE, 11 * 60);
    Acumulado previo =
        Acumulado.nuevo(residente.id(), periodo.id(), Fixtures.AYER)
            .sumar(210, new BigDecimal("10.50"), Fixtures.AYER);
    when(acumulados.buscar(residente.id(), periodo.id())).thenReturn(Optional.of(previo));
    when(acumulados.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

    EstanciaDeVehiculo resultado = casoDeUso.ejecutar("RES001");

    assertThat(resultado.estancia().importe()).isEqualByComparingTo("0.55");
    assertThat(resultado.momentoCobro()).isEqualTo(MomentoCobro.INFORME_MENSUAL);
    ArgumentCaptor<Acumulado> guardado = ArgumentCaptor.forClass(Acumulado.class);
    verify(acumulados).guardar(guardado.capture());
    assertThat(guardado.getValue().id()).isEqualTo(previo.id());
    assertThat(guardado.getValue().minutos()).isEqualTo(221);
    assertThat(guardado.getValue().importe()).isEqualByComparingTo("11.05");
  }

  @Test
  void residenteSinAcumuladoCreaLaFila() {
    Vehiculo residente = dentroDesdeHace("RES002", TipoVehiculo.RESIDENTE, 20 * 60);
    when(acumulados.buscar(residente.id(), periodo.id())).thenReturn(Optional.empty());
    when(acumulados.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

    casoDeUso.ejecutar("RES002");

    ArgumentCaptor<Acumulado> guardado = ArgumentCaptor.forClass(Acumulado.class);
    verify(acumulados).guardar(guardado.capture());
    assertThat(guardado.getValue().minutos()).isEqualTo(20);
    assertThat(guardado.getValue().importe()).isEqualByComparingTo("1.00");
    assertThat(guardado.getValue().periodoId()).isEqualTo(periodo.id());
  }

  @Test
  void oficialSaleSinCobroNiAcumulado() {
    dentroDesdeHace("OFI002", TipoVehiculo.OFICIAL, 480 * 60);

    EstanciaDeVehiculo resultado = casoDeUso.ejecutar("OFI002");

    assertThat(resultado.estancia().minutos()).isEqualTo(480);
    assertThat(resultado.estancia().importe()).isEqualByComparingTo("0.00");
    assertThat(resultado.momentoCobro()).isEqualTo(MomentoCobro.NINGUNO);
    verify(acumulados, never()).buscar(any(), any());
    verify(acumulados, never()).guardar(any());
  }

  @Test
  void rechazaSalidaSinEntrada() {
    Vehiculo fuera = Fixtures.vehiculo("RES003", TipoVehiculo.RESIDENTE);
    when(vehiculos.buscarPorPlacaParaActualizar(fuera.placa())).thenReturn(Optional.of(fuera));

    assertThatThrownBy(() -> casoDeUso.ejecutar("RES003"))
        .isInstanceOf(VehiculoNoDentroException.class);
    verify(estancias, never()).guardar(any());
  }

  @Test
  void rechazaSalidaDePlacaDesconocida() {
    when(vehiculos.buscarPorPlacaParaActualizar(Placa.de("ZZZ999"))).thenReturn(Optional.empty());

    assertThatThrownBy(() -> casoDeUso.ejecutar("ZZZ999"))
        .isInstanceOf(VehiculoNoEncontradoException.class);
  }
}
