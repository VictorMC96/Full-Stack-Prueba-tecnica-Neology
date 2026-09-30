package mx.neology.acceso.application;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import mx.neology.acceso.application.modelo.EstanciaDeVehiculo;
import mx.neology.acceso.application.puerto.AcumuladoRepositorio;
import mx.neology.acceso.application.puerto.EstanciaRepositorio;
import mx.neology.acceso.application.puerto.PeriodoRepositorio;
import mx.neology.acceso.application.puerto.TipoVehiculoRepositorio;
import mx.neology.acceso.application.puerto.Transaccion;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.Acumulado;
import mx.neology.acceso.domain.Estancia;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.Periodo;
import mx.neology.acceso.domain.Placa;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.Vehiculo;
import mx.neology.acceso.domain.cobro.CalculadoraCobro;
import mx.neology.acceso.domain.excepcion.VehiculoNoDentroException;
import mx.neology.acceso.domain.excepcion.VehiculoNoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Cierra la estancia abierta, calcula el cobro y acumula si la estrategia cobra en el informe. */
public final class RegistrarSalida {

  private static final Logger log = LoggerFactory.getLogger(RegistrarSalida.class);

  private final VehiculoRepositorio vehiculos;
  private final EstanciaRepositorio estancias;
  private final PeriodoRepositorio periodos;
  private final AcumuladoRepositorio acumulados;
  private final TipoVehiculoRepositorio tipos;
  private final CalculadoraCobro calculadora;
  private final Transaccion transaccion;
  private final Clock clock;

  public RegistrarSalida(
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

  public EstanciaDeVehiculo ejecutar(String placaCruda) {
    Placa placa = Placa.de(placaCruda);
    return transaccion.ejecutar(
        () -> {
          OffsetDateTime ahora = OffsetDateTime.now(clock);
          Vehiculo vehiculo =
              vehiculos
                  .buscarPorPlacaParaActualizar(placa)
                  .orElseThrow(() -> new VehiculoNoEncontradoException(placa.valor()));
          if (!vehiculo.dentro()) {
            throw new VehiculoNoDentroException(placa.valor());
          }
          Estancia abierta =
              estancias
                  .buscarPorId(vehiculo.estanciaAbiertaId())
                  .orElseThrow(() -> new VehiculoNoDentroException(placa.valor()));
          TipoVehiculo tipo = Tipos.de(tipos, vehiculo);
          long minutos = Estancia.minutosCompletos(abierta.entrada(), ahora);
          BigDecimal importe = calculadora.calcular(tipo, minutos);
          Periodo periodo = periodos.activo();
          Estancia cerrada =
              estancias.guardar(abierta.cerrar(ahora, minutos, importe, periodo.id()));
          vehiculos.guardar(vehiculo.sinEstanciaAbierta(ahora));
          MomentoCobro momento = calculadora.momento(tipo);
          if (momento == MomentoCobro.INFORME_MENSUAL) {
            Acumulado acumulado =
                acumulados
                    .buscar(vehiculo.id(), periodo.id())
                    .orElseGet(() -> Acumulado.nuevo(vehiculo.id(), periodo.id(), ahora));
            acumulados.guardar(acumulado.sumar(minutos, importe, ahora));
          }
          log.info(
              "evento=salida placa={} tipo={} minutos={} importe={} momento={}",
              placa,
              tipo.codigo(),
              minutos,
              importe,
              momento);
          return new EstanciaDeVehiculo(cerrada, vehiculo.placa(), tipo, momento);
        });
  }
}
