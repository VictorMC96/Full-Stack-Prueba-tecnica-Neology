package mx.neology.acceso.application;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import mx.neology.acceso.application.modelo.ConsultaPagina;
import mx.neology.acceso.application.modelo.DetalleVehiculo;
import mx.neology.acceso.application.modelo.EstanciaDeVehiculo;
import mx.neology.acceso.application.modelo.Pagina;
import mx.neology.acceso.application.modelo.VehiculoConTipo;
import mx.neology.acceso.application.puerto.AcumuladoRepositorio;
import mx.neology.acceso.application.puerto.EstanciaRepositorio;
import mx.neology.acceso.application.puerto.PeriodoRepositorio;
import mx.neology.acceso.application.puerto.TipoVehiculoRepositorio;
import mx.neology.acceso.application.puerto.Transaccion;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.Acumulado;
import mx.neology.acceso.domain.CodigoTipo;
import mx.neology.acceso.domain.Estancia;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.Periodo;
import mx.neology.acceso.domain.Placa;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.Vehiculo;
import mx.neology.acceso.domain.cobro.CalculadoraCobro;
import mx.neology.acceso.domain.excepcion.VehiculoNoEncontradoException;

/** Consultas de solo lectura: listado paginado, detalle e historial de estancias. */
public final class ConsultarVehiculos {

  private final VehiculoRepositorio vehiculos;
  private final EstanciaRepositorio estancias;
  private final PeriodoRepositorio periodos;
  private final AcumuladoRepositorio acumulados;
  private final TipoVehiculoRepositorio tipos;
  private final CalculadoraCobro calculadora;
  private final Transaccion transaccion;
  private final Clock clock;

  public ConsultarVehiculos(
      VehiculoRepositorio vehiculos,
      EstanciaRepositorio estancias,
      PeriodoRepositorio periodos,
      AcumuladoRepositorio acumulados,
      TipoVehiculoRepositorio tipos,
      CalculadoraCobro calculadora,
      Transaccion transaccion,
      Clock clock) {
    this.vehiculos = vehiculos;
    this.estancias = estancias;
    this.periodos = periodos;
    this.acumulados = acumulados;
    this.tipos = tipos;
    this.calculadora = calculadora;
    this.transaccion = transaccion;
    this.clock = clock;
  }

  public Pagina<VehiculoConTipo> listar(String filtroPlaca, ConsultaPagina consulta) {
    String filtro = Placa.normalizar(filtroPlaca);
    return transaccion.consultar(
        () -> {
          Map<CodigoTipo, TipoVehiculo> catalogo =
              tipos.listar().stream()
                  .collect(Collectors.toMap(TipoVehiculo::codigo, Function.identity()));
          return vehiculos
              .listar(filtro, consulta)
              .map(v -> new VehiculoConTipo(v, tipoDe(catalogo, v)));
        });
  }

  public DetalleVehiculo detalle(String placaCruda) {
    Placa placa = Placa.de(placaCruda);
    return transaccion.consultar(
        () -> {
          Vehiculo vehiculo = buscar(placa);
          TipoVehiculo tipo = Tipos.de(tipos, vehiculo);
          Optional<EstanciaDeVehiculo> abierta =
              Optional.ofNullable(vehiculo.estanciaAbiertaId())
                  .flatMap(estancias::buscarPorId)
                  .map(e -> deVehiculo(e, vehiculo, tipo));
          Optional<Acumulado> acumulado = Optional.empty();
          if (calculadora.momento(tipo) == MomentoCobro.INFORME_MENSUAL) {
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
          return new DetalleVehiculo(new VehiculoConTipo(vehiculo, tipo), abierta, acumulado);
        });
  }

  public Pagina<EstanciaDeVehiculo> estancias(String placaCruda, ConsultaPagina consulta) {
    Placa placa = Placa.de(placaCruda);
    return transaccion.consultar(
        () -> {
          Vehiculo vehiculo = buscar(placa);
          TipoVehiculo tipo = Tipos.de(tipos, vehiculo);
          return estancias
              .listarPorVehiculo(vehiculo.id(), consulta)
              .map(e -> deVehiculo(e, vehiculo, tipo));
        });
  }

  private Vehiculo buscar(Placa placa) {
    return vehiculos
        .buscarPorPlaca(placa)
        .orElseThrow(() -> new VehiculoNoEncontradoException(placa.valor()));
  }

  private static TipoVehiculo tipoDe(Map<CodigoTipo, TipoVehiculo> catalogo, Vehiculo vehiculo) {
    TipoVehiculo tipo = catalogo.get(vehiculo.tipo());
    if (tipo == null) {
      throw new IllegalStateException(
          "El vehículo " + vehiculo.placa() + " tiene el tipo desconocido " + vehiculo.tipo());
    }
    return tipo;
  }

  private EstanciaDeVehiculo deVehiculo(Estancia estancia, Vehiculo vehiculo, TipoVehiculo tipo) {
    return new EstanciaDeVehiculo(estancia, vehiculo.placa(), tipo, calculadora.momento(tipo));
  }
}
