package mx.neology.acceso.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import mx.neology.acceso.application.modelo.ConsultaPagina;
import mx.neology.acceso.application.modelo.DetalleVehiculo;
import mx.neology.acceso.application.modelo.EstanciaDeVehiculo;
import mx.neology.acceso.application.modelo.Pagina;
import mx.neology.acceso.application.modelo.VehiculoConTipo;
import mx.neology.acceso.application.puerto.AcumuladoRepositorio;
import mx.neology.acceso.application.puerto.EstanciaRepositorio;
import mx.neology.acceso.application.puerto.PeriodoRepositorio;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.Acumulado;
import mx.neology.acceso.domain.CodigoTipo;
import mx.neology.acceso.domain.Estancia;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.Periodo;
import mx.neology.acceso.domain.Placa;
import mx.neology.acceso.domain.Vehiculo;
import mx.neology.acceso.domain.excepcion.VehiculoNoEncontradoException;
import mx.neology.acceso.soporte.TransaccionDirecta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsultarVehiculosTest {

  @Mock VehiculoRepositorio vehiculos;
  @Mock EstanciaRepositorio estancias;
  @Mock PeriodoRepositorio periodos;
  @Mock AcumuladoRepositorio acumulados;
  ConsultarVehiculos casoDeUso;
  Periodo periodo = Fixtures.periodoActivo();

  @BeforeEach
  void setUp() {
    casoDeUso =
        new ConsultarVehiculos(
            vehiculos,
            estancias,
            periodos,
            acumulados,
            Fixtures.catalogo(),
            Fixtures.calculadora(),
            new TransaccionDirecta(),
            Fixtures.RELOJ);
  }

  @Test
  void detalleDeResidenteConAcumuladoYEstanciaAbierta() {
    Vehiculo base = Fixtures.vehiculo("RES001", Fixtures.RESIDENTE);
    Estancia abierta = Estancia.abrir(base.id(), Fixtures.AHORA);
    Vehiculo residente = base.conEstanciaAbierta(abierta.id(), Fixtures.AHORA);
    when(vehiculos.buscarPorPlaca(residente.placa())).thenReturn(Optional.of(residente));
    when(estancias.buscarPorId(abierta.id())).thenReturn(Optional.of(abierta));
    when(periodos.activo()).thenReturn(periodo);
    Acumulado acumulado =
        Acumulado.nuevo(residente.id(), periodo.id(), Fixtures.AYER)
            .sumar(210, new BigDecimal("10.50"), Fixtures.AYER);
    when(acumulados.buscar(residente.id(), periodo.id())).thenReturn(Optional.of(acumulado));

    DetalleVehiculo detalle = casoDeUso.detalle("res-001");

    assertThat(detalle.vehiculo().vehiculo().dentro()).isTrue();
    assertThat(detalle.vehiculo().tipo().nombre()).isEqualTo("Residente");
    assertThat(detalle.estanciaAbierta()).isPresent();
    assertThat(detalle.estanciaAbierta().get().momentoCobro())
        .isEqualTo(MomentoCobro.INFORME_MENSUAL);
    assertThat(detalle.acumuladoPeriodo()).isPresent();
    assertThat(detalle.acumuladoPeriodo().get().minutos()).isEqualTo(210);
  }

  @Test
  void detalleDeResidenteSinAcumuladoDevuelveCeros() {
    Vehiculo residente = Fixtures.vehiculo("RES009", Fixtures.RESIDENTE);
    when(vehiculos.buscarPorPlaca(residente.placa())).thenReturn(Optional.of(residente));
    when(periodos.activo()).thenReturn(periodo);
    when(acumulados.buscar(residente.id(), periodo.id())).thenReturn(Optional.empty());

    DetalleVehiculo detalle = casoDeUso.detalle("RES009");

    assertThat(detalle.estanciaAbierta()).isEmpty();
    assertThat(detalle.acumuladoPeriodo()).isPresent();
    assertThat(detalle.acumuladoPeriodo().get().minutos()).isZero();
    assertThat(detalle.acumuladoPeriodo().get().importe()).isEqualByComparingTo("0.00");
  }

  @Test
  void detalleDeNoResidenteNoLlevaAcumulado() {
    Vehiculo noResidente = Fixtures.vehiculo("NOR001", Fixtures.NO_RESIDENTE);
    when(vehiculos.buscarPorPlaca(noResidente.placa())).thenReturn(Optional.of(noResidente));

    DetalleVehiculo detalle = casoDeUso.detalle("NOR001");

    assertThat(detalle.acumuladoPeriodo()).isEmpty();
    verify(periodos, never()).activo();
  }

  @Test
  void placaInexistenteLanzaNoEncontrado() {
    when(vehiculos.buscarPorPlaca(Placa.de("ZZZ999"))).thenReturn(Optional.empty());

    assertThatThrownBy(() -> casoDeUso.detalle("ZZZ999"))
        .isInstanceOf(VehiculoNoEncontradoException.class);
    assertThatThrownBy(
            () -> casoDeUso.estancias("ZZZ999", new ConsultaPagina(0, 10, "entrada", false)))
        .isInstanceOf(VehiculoNoEncontradoException.class);
  }

  @Test
  void listarDecoraConElTipoYEstanciasConservanLaPaginacion() {
    ConsultaPagina consulta = new ConsultaPagina(0, 10, "placa", true);
    Vehiculo residente = Fixtures.vehiculo("RES001", Fixtures.RESIDENTE);
    when(vehiculos.listar("RES", consulta))
        .thenReturn(new Pagina<>(List.of(residente), 0, 10, 1, 1));
    Pagina<VehiculoConTipo> listado = casoDeUso.listar(" re-s ", consulta);
    assertThat(listado.contenido()).hasSize(1);
    assertThat(listado.contenido().get(0).vehiculo()).isEqualTo(residente);
    assertThat(listado.contenido().get(0).tipo()).isEqualTo(Fixtures.RESIDENTE);

    ConsultaPagina porEntrada = new ConsultaPagina(0, 5, "entrada", false);
    Estancia estancia = Estancia.abrir(residente.id(), Fixtures.AHORA);
    when(vehiculos.buscarPorPlaca(residente.placa())).thenReturn(Optional.of(residente));
    when(estancias.listarPorVehiculo(residente.id(), porEntrada))
        .thenReturn(new Pagina<>(List.of(estancia), 0, 5, 1, 1));
    Pagina<EstanciaDeVehiculo> pagina = casoDeUso.estancias("RES001", porEntrada);
    assertThat(pagina.totalElementos()).isEqualTo(1);
    assertThat(pagina.contenido().get(0).placa().valor()).isEqualTo("RES001");
    assertThat(pagina.contenido().get(0).momentoCobro()).isEqualTo(MomentoCobro.INFORME_MENSUAL);
  }

  @Test
  void unVehiculoConTipoDesconocidoEsUnErrorInterno() {
    ConsultaPagina consulta = new ConsultaPagina(0, 10, "placa", true);
    Vehiculo raro =
        new Vehiculo(
            UUID.randomUUID(),
            Placa.de("RAR001"),
            new CodigoTipo("ZZZ"),
            null,
            Fixtures.AYER,
            Fixtures.AYER);
    when(vehiculos.listar("", consulta)).thenReturn(new Pagina<>(List.of(raro), 0, 10, 1, 1));
    assertThatThrownBy(() -> casoDeUso.listar(null, consulta))
        .isInstanceOf(IllegalStateException.class);
    when(vehiculos.buscarPorPlaca(raro.placa())).thenReturn(Optional.of(raro));
    assertThatThrownBy(() -> casoDeUso.detalle("RAR001")).isInstanceOf(IllegalStateException.class);
  }
}
