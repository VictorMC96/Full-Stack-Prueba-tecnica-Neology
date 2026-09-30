package mx.neology.acceso.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.Placa;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.Vehiculo;
import mx.neology.acceso.domain.excepcion.PlacaInvalidaException;
import mx.neology.acceso.domain.excepcion.VehiculoYaRegistradoException;
import mx.neology.acceso.soporte.TransaccionDirecta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistrarVehiculoTest {

  @Mock VehiculoRepositorio vehiculos;
  RegistrarVehiculo casoDeUso;

  @BeforeEach
  void setUp() {
    casoDeUso = new RegistrarVehiculo(vehiculos, new TransaccionDirecta(), Fixtures.RELOJ);
    lenient().when(vehiculos.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
  }

  @Test
  void creaUnVehiculoNuevoConLaPlacaNormalizada() {
    when(vehiculos.buscarPorPlacaParaActualizar(Placa.de("ABC123"))).thenReturn(Optional.empty());

    Vehiculo creado = casoDeUso.ejecutar("abc-123", TipoVehiculo.OFICIAL);

    assertThat(creado.placa().valor()).isEqualTo("ABC123");
    assertThat(creado.tipo()).isEqualTo(TipoVehiculo.OFICIAL);
    assertThat(creado.dentro()).isFalse();
    assertThat(creado.createdAt()).isEqualTo(Fixtures.AHORA);
  }

  @Test
  void reclasificaUnNoResidenteConservandoElId() {
    Vehiculo existente = Fixtures.vehiculo("NOR001", TipoVehiculo.NO_RESIDENTE);
    when(vehiculos.buscarPorPlacaParaActualizar(existente.placa()))
        .thenReturn(Optional.of(existente));

    Vehiculo resultado = casoDeUso.ejecutar("nor001", TipoVehiculo.RESIDENTE);

    ArgumentCaptor<Vehiculo> guardado = ArgumentCaptor.forClass(Vehiculo.class);
    verify(vehiculos).guardar(guardado.capture());
    assertThat(guardado.getValue().id()).isEqualTo(existente.id());
    assertThat(resultado.tipo()).isEqualTo(TipoVehiculo.RESIDENTE);
    assertThat(resultado.updatedAt()).isEqualTo(Fixtures.AHORA);
  }

  @Test
  void rechazaAltaDeUnaPlacaYaOficialOResidente() {
    Vehiculo oficial = Fixtures.vehiculo("OFI001", TipoVehiculo.OFICIAL);
    when(vehiculos.buscarPorPlacaParaActualizar(oficial.placa())).thenReturn(Optional.of(oficial));

    assertThatThrownBy(() -> casoDeUso.ejecutar("OFI001", TipoVehiculo.RESIDENTE))
        .isInstanceOf(VehiculoYaRegistradoException.class);
    verify(vehiculos, never()).guardar(any());
  }

  @Test
  void rechazaAltaDeNoResidenteSiYaExisteConCualquierTipo() {
    Vehiculo existente = Fixtures.vehiculo("NOR001", TipoVehiculo.NO_RESIDENTE);
    when(vehiculos.buscarPorPlacaParaActualizar(existente.placa()))
        .thenReturn(Optional.of(existente));

    assertThatThrownBy(() -> casoDeUso.ejecutar("NOR001", TipoVehiculo.NO_RESIDENTE))
        .isInstanceOf(VehiculoYaRegistradoException.class);
  }

  @Test
  void rechazaPlacaInvalidaAntesDeTocarElRepositorio() {
    assertThatThrownBy(() -> casoDeUso.ejecutar("A<1>", TipoVehiculo.OFICIAL))
        .isInstanceOf(PlacaInvalidaException.class);
    verify(vehiculos, never()).buscarPorPlacaParaActualizar(any());
  }
}
