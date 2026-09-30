package mx.neology.acceso.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import mx.neology.acceso.application.modelo.EstanciaDeVehiculo;
import mx.neology.acceso.application.puerto.EstanciaRepositorio;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.Placa;
import mx.neology.acceso.domain.Vehiculo;
import mx.neology.acceso.domain.excepcion.VehiculoYaDentroException;
import mx.neology.acceso.soporte.TransaccionDirecta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistrarEntradaTest {

  @Mock VehiculoRepositorio vehiculos;
  @Mock EstanciaRepositorio estancias;
  RegistrarEntrada casoDeUso;

  @BeforeEach
  void setUp() {
    casoDeUso =
        new RegistrarEntrada(
            vehiculos,
            estancias,
            Fixtures.catalogo(),
            Fixtures.calculadora(),
            new TransaccionDirecta(),
            Fixtures.RELOJ);
  }

  @Test
  void registraEntradaDeUnVehiculoConocido() {
    Vehiculo residente = Fixtures.vehiculo("RES001", Fixtures.RESIDENTE);
    when(vehiculos.buscarPorPlacaParaActualizar(residente.placa()))
        .thenReturn(Optional.of(residente));
    when(estancias.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
    when(vehiculos.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

    EstanciaDeVehiculo resultado = casoDeUso.ejecutar("res-001");

    assertThat(resultado.estancia().entrada()).isEqualTo(Fixtures.AHORA);
    assertThat(resultado.estancia().abierta()).isTrue();
    assertThat(resultado.tipo()).isEqualTo(Fixtures.RESIDENTE);
    assertThat(resultado.momentoCobro()).isEqualTo(MomentoCobro.INFORME_MENSUAL);
    ArgumentCaptor<Vehiculo> actualizado = ArgumentCaptor.forClass(Vehiculo.class);
    verify(vehiculos).guardar(actualizado.capture());
    assertThat(actualizado.getValue().estanciaAbiertaId()).isEqualTo(resultado.estancia().id());
  }

  @Test
  void daDeAltaConElTipoProvisionalUnaPlacaDesconocida() {
    when(vehiculos.buscarPorPlacaParaActualizar(Placa.de("NUEVA1"))).thenReturn(Optional.empty());
    when(estancias.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
    when(vehiculos.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

    EstanciaDeVehiculo resultado = casoDeUso.ejecutar("nueva-1");

    assertThat(resultado.tipo()).isEqualTo(Fixtures.NO_RESIDENTE);
    assertThat(resultado.momentoCobro()).isEqualTo(MomentoCobro.AL_SALIR);
    assertThat(resultado.placa().valor()).isEqualTo("NUEVA1");
  }

  @Test
  void rechazaEntradaSiElVehiculoYaEstaDentro() {
    Vehiculo dentro =
        Fixtures.vehiculo("OFI002", Fixtures.OFICIAL)
            .conEstanciaAbierta(UUID.randomUUID(), Fixtures.AYER);
    when(vehiculos.buscarPorPlacaParaActualizar(dentro.placa())).thenReturn(Optional.of(dentro));

    assertThatThrownBy(() -> casoDeUso.ejecutar("OFI002"))
        .isInstanceOf(VehiculoYaDentroException.class);
    verify(estancias, never()).guardar(any());
  }
}
