package mx.neology.acceso.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mx.neology.acceso.application.modelo.VehiculoConTipo;
import mx.neology.acceso.application.puerto.TipoVehiculoRepositorio;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.Placa;
import mx.neology.acceso.domain.Vehiculo;
import mx.neology.acceso.domain.excepcion.PlacaInvalidaException;
import mx.neology.acceso.domain.excepcion.TipoNoEncontradoException;
import mx.neology.acceso.domain.excepcion.ValidacionException;
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
  TipoVehiculoRepositorio tipos = Fixtures.catalogo();
  RegistrarVehiculo casoDeUso;

  @BeforeEach
  void setUp() {
    casoDeUso = new RegistrarVehiculo(vehiculos, tipos, new TransaccionDirecta(), Fixtures.RELOJ);
    lenient().when(vehiculos.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
  }

  @Test
  void creaUnVehiculoNuevoConLaPlacaYElCodigoNormalizados() {
    when(vehiculos.buscarPorPlacaParaActualizar(Placa.de("ABC123"))).thenReturn(Optional.empty());

    VehiculoConTipo creado = casoDeUso.ejecutar("abc-123", " oficial ");

    assertThat(creado.vehiculo().placa().valor()).isEqualTo("ABC123");
    assertThat(creado.vehiculo().tipo().valor()).isEqualTo("OFICIAL");
    assertThat(creado.tipo()).isEqualTo(Fixtures.OFICIAL);
    assertThat(creado.vehiculo().dentro()).isFalse();
    assertThat(creado.vehiculo().createdAt()).isEqualTo(Fixtures.AHORA);
  }

  @Test
  void reclasificaUnVehiculoDeTipoProvisionalConservandoElId() {
    Vehiculo existente = Fixtures.vehiculo("NOR001", Fixtures.NO_RESIDENTE);
    when(vehiculos.buscarPorPlacaParaActualizar(existente.placa()))
        .thenReturn(Optional.of(existente));

    VehiculoConTipo resultado = casoDeUso.ejecutar("nor001", "RESIDENTE");

    ArgumentCaptor<Vehiculo> guardado = ArgumentCaptor.forClass(Vehiculo.class);
    verify(vehiculos).guardar(guardado.capture());
    assertThat(guardado.getValue().id()).isEqualTo(existente.id());
    assertThat(resultado.vehiculo().tipo().valor()).isEqualTo("RESIDENTE");
    assertThat(resultado.tipo().nombre()).isEqualTo("Residente");
    assertThat(resultado.vehiculo().updatedAt()).isEqualTo(Fixtures.AHORA);
  }

  @Test
  void rechazaAltaDeUnaPlacaConTipoDefinitivo() {
    Vehiculo oficial = Fixtures.vehiculo("OFI001", Fixtures.OFICIAL);
    when(vehiculos.buscarPorPlacaParaActualizar(oficial.placa())).thenReturn(Optional.of(oficial));

    assertThatThrownBy(() -> casoDeUso.ejecutar("OFI001", "RESIDENTE"))
        .isInstanceOf(VehiculoYaRegistradoException.class);
    assertThatThrownBy(() -> casoDeUso.ejecutar("OFI001", "VISITANTE"))
        .isInstanceOf(VehiculoYaRegistradoException.class);
    verify(vehiculos, never()).guardar(any());
  }

  @Test
  void rechazaReclasificarAlTipoProvisional() {
    Vehiculo existente = Fixtures.vehiculo("NOR001", Fixtures.NO_RESIDENTE);
    when(vehiculos.buscarPorPlacaParaActualizar(existente.placa()))
        .thenReturn(Optional.of(existente));

    assertThatThrownBy(() -> casoDeUso.ejecutar("NOR001", "NO_RESIDENTE"))
        .isInstanceOf(VehiculoYaRegistradoException.class);
  }

  @Test
  void rechazaTipoInexistenteYEntradasInvalidasAntesDeTocarVehiculos() {
    assertThatThrownBy(() -> casoDeUso.ejecutar("ABC123", "ZZZ"))
        .isInstanceOf(TipoNoEncontradoException.class);
    assertThatThrownBy(() -> casoDeUso.ejecutar("A<1>", "OFICIAL"))
        .isInstanceOf(PlacaInvalidaException.class);
    assertThatThrownBy(() -> casoDeUso.ejecutar("ABC123", "1x"))
        .isInstanceOf(ValidacionException.class);
    verify(vehiculos, never()).buscarPorPlacaParaActualizar(any());
  }
}
