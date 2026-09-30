package mx.neology.acceso.application;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import mx.neology.acceso.application.modelo.ConsultaPagina;
import mx.neology.acceso.application.modelo.DetalleVehiculo;
import mx.neology.acceso.application.modelo.EstanciaDeVehiculo;
import mx.neology.acceso.application.modelo.Pagina;
import mx.neology.acceso.application.puerto.AcumuladoRepositorio;
import mx.neology.acceso.application.puerto.EstanciaRepositorio;
import mx.neology.acceso.application.puerto.PeriodoRepositorio;
import mx.neology.acceso.application.puerto.Transaccion;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.Acumulado;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.Periodo;
import mx.neology.acceso.domain.Placa;
import mx.neology.acceso.domain.Vehiculo;
import mx.neology.acceso.domain.cobro.CalculadoraCobro;
import mx.neology.acceso.domain.excepcion.VehiculoNoEncontradoException;

/** Consultas de solo lectura: listado paginado, detalle e historial de estancias. */
public final class ConsultarVehiculos {

  private final VehiculoRepositorio vehiculos;
  private final EstanciaRepositorio estancias;
  private final PeriodoRepositorio periodos;
  private final AcumuladoRepositorio acumulados;
  private final CalculadoraCobro calculadora;
  private final Transaccion transaccion;
  private final Clock clock;

  public ConsultarVehiculos(
      VehiculoRepositorio vehiculos,
      EstanciaRepositorio estancias,
      PeriodoRepositorio periodos,
      AcumuladoRepositorio acumulados,
      CalculadoraCobro calculadora,
      Transaccion transaccion,
      Clock clock) {
    this.vehiculos = vehiculos;
    this.estancias = estancias;
    this.periodos = periodos;
    this.acumulados = acumulados;
    this.calculadora = calculadora;
    this.transaccion = transaccion;
    this.clock = clock;
  }

  public Pagina<Vehiculo> listar(String filtroPlaca, ConsultaPagina consulta) {
    String filtro = Placa.normalizar(filtroPlaca);
    return transaccion.consultar(() -> vehiculos.listar(filtro, consulta));
  }

  public DetalleVehiculo detalle(String placaCruda) {
    Placa placa = Placa.de(placaCruda);
    return transaccion.consultar(
        () -> {
          Vehiculo vehiculo = buscar(placa);
          Optional<EstanciaDeVehiculo> abierta =
              Optional.ofNullable(vehiculo.estanciaAbiertaId())
                  .flatMap(estancias::buscarPorId)
                  .map(e -> deVehiculo(e, vehiculo));
          Optional<Acumulado> acumulado = Optional.empty();
          if (calculadora.momento(vehiculo.tipo()) == MomentoCobro.INFORME_MENSUAL) {
            Periodo periodo = periodos.activo();
            acumulado =
                Optional.of(
                    acumulados
                        .buscar(vehiculo.id(), periodo.id())
                        .orElseGet(
                            () ->
                                Acumulado.nuevo(
                                    vehiculo.id(), periodo.id(), OffsetDateTime.now(clock))));
          }
          return new DetalleVehiculo(vehiculo, abierta, acumulado);
        });
  }

  public Pagina<EstanciaDeVehiculo> estancias(String placaCruda, ConsultaPagina consulta) {
    Placa placa = Placa.de(placaCruda);
    return transaccion.consultar(
        () -> {
          Vehiculo vehiculo = buscar(placa);
          return estancias
              .listarPorVehiculo(vehiculo.id(), consulta)
              .map(e -> deVehiculo(e, vehiculo));
        });
  }

  private Vehiculo buscar(Placa placa) {
    return vehiculos
        .buscarPorPlaca(placa)
        .orElseThrow(() -> new VehiculoNoEncontradoException(placa.valor()));
  }

  private EstanciaDeVehiculo deVehiculo(
      mx.neology.acceso.domain.Estancia estancia, Vehiculo vehiculo) {
    return new EstanciaDeVehiculo(
        estancia, vehiculo.placa(), vehiculo.tipo(), calculadora.momento(vehiculo.tipo()));
  }
}
