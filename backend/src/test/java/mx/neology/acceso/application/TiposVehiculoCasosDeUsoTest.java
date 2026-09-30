package mx.neology.acceso.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mx.neology.acceso.application.modelo.TipoVehiculoConUso;
import mx.neology.acceso.application.puerto.TipoVehiculoRepositorio;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.CodigoTipo;
import mx.neology.acceso.domain.ModeloCobro;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.excepcion.TipoEnUsoException;
import mx.neology.acceso.domain.excepcion.TipoIntegradoException;
import mx.neology.acceso.domain.excepcion.TipoNoEncontradoException;
import mx.neology.acceso.domain.excepcion.TipoYaRegistradoException;
import mx.neology.acceso.domain.excepcion.ValidacionException;
import mx.neology.acceso.soporte.TransaccionDirecta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TiposVehiculoCasosDeUsoTest {

  @Mock VehiculoRepositorio vehiculos;
  TipoVehiculoRepositorio tipos = Fixtures.catalogo();
  TransaccionDirecta transaccion = new TransaccionDirecta();

  private RegistrarTipoVehiculo registrar() {
    return new RegistrarTipoVehiculo(tipos, Fixtures.calculadora(), transaccion, Fixtures.RELOJ);
  }

  private ActualizarTipoVehiculo actualizar() {
    return new ActualizarTipoVehiculo(
        tipos, vehiculos, Fixtures.calculadora(), transaccion, Fixtures.RELOJ);
  }

  private EliminarTipoVehiculo eliminar() {
    return new EliminarTipoVehiculo(tipos, vehiculos, transaccion);
  }

  @Test
  void creaUnTipoNormalizandoElCodigo() {
    when(tipos.buscar(new CodigoTipo("PROVEEDOR"))).thenReturn(Optional.empty());
    when(tipos.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

    TipoVehiculo creado =
        registrar()
            .ejecutar(
                " proveedor ",
                " Proveedor ",
                ModeloCobro.POR_MINUTO_AL_SALIR,
                new BigDecimal("0.3"));

    assertThat(creado.codigo().valor()).isEqualTo("PROVEEDOR");
    assertThat(creado.nombre()).isEqualTo("Proveedor");
    assertThat(creado.tarifaPorMinuto()).isEqualByComparingTo("0.30");
    assertThat(creado.tarifaPorMinuto().scale()).isEqualTo(2);
    assertThat(creado.provisional()).isFalse();
    assertThat(creado.integrado()).isFalse();
    assertThat(creado.createdAt()).isEqualTo(Fixtures.AHORA);
  }

  @Test
  void rechazaCodigoDuplicadoYEntradasInvalidas() {
    assertThatThrownBy(
            () ->
                registrar()
                    .ejecutar("RESIDENTE", "Otro", ModeloCobro.POR_MINUTO_AL_SALIR, BigDecimal.ONE))
        .isInstanceOf(TipoYaRegistradoException.class);
    assertThatThrownBy(
            () ->
                registrar()
                    .ejecutar("NUEVO", "Nuevo", ModeloCobro.SIN_COBRO, new BigDecimal("0.10")))
        .isInstanceOf(ValidacionException.class)
        .satisfies(
            ex -> assertThat(((ValidacionException) ex).campo()).isEqualTo("tarifaPorMinuto"));
    assertThatThrownBy(() -> registrar().ejecutar("NUEVO", "Nuevo", null, BigDecimal.ZERO))
        .isInstanceOf(ValidacionException.class);
    assertThatThrownBy(
            () -> registrar().ejecutar("NUEVO", "X", ModeloCobro.SIN_COBRO, BigDecimal.ZERO))
        .isInstanceOf(ValidacionException.class)
        .satisfies(ex -> assertThat(((ValidacionException) ex).campo()).isEqualTo("nombre"));
    assertThatThrownBy(
            () ->
                registrar()
                    .ejecutar(
                        "NUEVO", "Nuevo", ModeloCobro.POR_MINUTO_AL_SALIR, new BigDecimal("0.123")))
        .isInstanceOf(ValidacionException.class);
    assertThatThrownBy(
            () ->
                registrar()
                    .ejecutar(
                        "NUEVO", "Nuevo", ModeloCobro.POR_MINUTO_AL_SALIR, new BigDecimal("-1")))
        .isInstanceOf(ValidacionException.class);
    verify(tipos, never()).guardar(any());
  }

  @Test
  void actualizaNombreModeloYTarifaDeUnTipoCreado() {
    when(tipos.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
    when(vehiculos.contarPorTipo(new CodigoTipo("VISITANTE"))).thenReturn(2L);

    TipoVehiculoConUso resultado =
        actualizar()
            .ejecutar(
                "visitante", "Visita", ModeloCobro.POR_MINUTO_ACUMULADO, new BigDecimal("0.10"));

    assertThat(resultado.tipo().nombre()).isEqualTo("Visita");
    assertThat(resultado.tipo().modeloCobro()).isEqualTo(ModeloCobro.POR_MINUTO_ACUMULADO);
    assertThat(resultado.tipo().tarifaPorMinuto()).isEqualByComparingTo("0.10");
    assertThat(resultado.tipo().createdAt()).isEqualTo(Fixtures.AYER);
    assertThat(resultado.tipo().updatedAt()).isEqualTo(Fixtures.AHORA);
    assertThat(resultado.vehiculos()).isEqualTo(2);
  }

  @Test
  void unTipoIntegradoCambiaTarifaPeroNoModelo() {
    when(tipos.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
    when(vehiculos.contarPorTipo(any())).thenReturn(0L);

    TipoVehiculoConUso resultado =
        actualizar()
            .ejecutar(
                "RESIDENTE", "Residente", ModeloCobro.POR_MINUTO_ACUMULADO, new BigDecimal("0.07"));
    assertThat(resultado.tipo().tarifaPorMinuto()).isEqualByComparingTo("0.07");
    assertThat(resultado.tipo().integrado()).isTrue();

    assertThatThrownBy(
            () ->
                actualizar()
                    .ejecutar("RESIDENTE", "Residente", ModeloCobro.SIN_COBRO, BigDecimal.ZERO))
        .isInstanceOf(TipoIntegradoException.class);
    assertThatThrownBy(
            () -> actualizar().ejecutar("ZZZ", "Nada", ModeloCobro.SIN_COBRO, BigDecimal.ZERO))
        .isInstanceOf(TipoNoEncontradoException.class);
    assertThatThrownBy(() -> actualizar().ejecutar("OFICIAL", "Oficial", null, BigDecimal.ZERO))
        .isInstanceOf(ValidacionException.class);
    assertThatThrownBy(
            () ->
                actualizar().ejecutar("OFICIAL", "Oficial", ModeloCobro.SIN_COBRO, BigDecimal.ONE))
        .isInstanceOf(ValidacionException.class);
  }

  @Test
  void eliminaSoloTiposCreadosSinVehiculos() {
    when(vehiculos.contarPorTipo(new CodigoTipo("VISITANTE"))).thenReturn(0L);
    eliminar().ejecutar("visitante");
    ArgumentCaptor<CodigoTipo> borrado = ArgumentCaptor.forClass(CodigoTipo.class);
    verify(tipos).eliminar(borrado.capture());
    assertThat(borrado.getValue().valor()).isEqualTo("VISITANTE");

    when(vehiculos.contarPorTipo(new CodigoTipo("VISITANTE"))).thenReturn(3L);
    assertThatThrownBy(() -> eliminar().ejecutar("VISITANTE"))
        .isInstanceOf(TipoEnUsoException.class)
        .hasMessageContaining("3");
    assertThatThrownBy(() -> eliminar().ejecutar("OFICIAL"))
        .isInstanceOf(TipoIntegradoException.class);
    assertThatThrownBy(() -> eliminar().ejecutar("ZZZ"))
        .isInstanceOf(TipoNoEncontradoException.class);
  }

  @Test
  void consultaElCatalogoConElUsoDeCadaTipo() {
    when(vehiculos.conteoPorTipo())
        .thenReturn(
            java.util.Map.of(new CodigoTipo("RESIDENTE"), 3L, new CodigoTipo("OFICIAL"), 2L));
    ConsultarTiposVehiculo consultar = new ConsultarTiposVehiculo(tipos, vehiculos, transaccion);

    List<TipoVehiculoConUso> lista = consultar.listar();

    assertThat(lista)
        .extracting(t -> t.tipo().codigo().valor())
        .containsExactly("NO_RESIDENTE", "OFICIAL", "RESIDENTE", "VISITANTE");
    assertThat(lista).extracting(TipoVehiculoConUso::vehiculos).containsExactly(0L, 2L, 3L, 0L);

    when(vehiculos.contarPorTipo(new CodigoTipo("VISITANTE"))).thenReturn(1L);
    assertThat(consultar.buscar("visitante").vehiculos()).isEqualTo(1);
    assertThatThrownBy(() -> consultar.buscar("ZZZ")).isInstanceOf(TipoNoEncontradoException.class);
  }
}
