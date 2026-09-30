package mx.neology.acceso.application;

import java.time.Clock;
import java.time.OffsetDateTime;
import mx.neology.acceso.application.modelo.EstanciaDeVehiculo;
import mx.neology.acceso.application.puerto.EstanciaRepositorio;
import mx.neology.acceso.application.puerto.TipoVehiculoRepositorio;
import mx.neology.acceso.application.puerto.Transaccion;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.Estancia;
import mx.neology.acceso.domain.Placa;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.Vehiculo;
import mx.neology.acceso.domain.cobro.CalculadoraCobro;
import mx.neology.acceso.domain.excepcion.VehiculoYaDentroException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Registra la entrada; una placa desconocida se da de alta con el tipo provisional. */
public final class RegistrarEntrada {

  private static final Logger log = LoggerFactory.getLogger(RegistrarEntrada.class);

  private final VehiculoRepositorio vehiculos;
  private final EstanciaRepositorio estancias;
  private final TipoVehiculoRepositorio tipos;
  private final CalculadoraCobro calculadora;
  private final Transaccion transaccion;
  private final Clock clock;

  public RegistrarEntrada(
      VehiculoRepositorio vehiculos,
      EstanciaRepositorio estancias,
      TipoVehiculoRepositorio tipos,
      CalculadoraCobro calculadora,
      Transaccion transaccion,
      Clock clock) {
    this.vehiculos = vehiculos;
    this.estancias = estancias;
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
                  .orElseGet(
                      () -> {
                        TipoVehiculo provisional = tipos.provisional();
                        Vehiculo nuevo =
                            vehiculos.guardar(Vehiculo.nuevo(placa, provisional.codigo(), ahora));
                        log.info("evento=alta_automatica placa={} tipo={}", placa, nuevo.tipo());
                        return nuevo;
                      });
          if (vehiculo.dentro()) {
            throw new VehiculoYaDentroException(placa.valor());
          }
          TipoVehiculo tipo = Tipos.de(tipos, vehiculo);
          Estancia estancia = estancias.guardar(Estancia.abrir(vehiculo.id(), ahora));
          vehiculos.guardar(vehiculo.conEstanciaAbierta(estancia.id(), ahora));
          log.info(
              "evento=entrada placa={} tipo={} estancia={} entrada={}",
              placa,
              tipo.codigo(),
              estancia.id(),
              ahora);
          return new EstanciaDeVehiculo(
              estancia, vehiculo.placa(), tipo, calculadora.momento(tipo));
        });
  }
}
