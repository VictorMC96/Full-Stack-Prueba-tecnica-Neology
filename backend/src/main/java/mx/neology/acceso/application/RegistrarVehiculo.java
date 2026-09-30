package mx.neology.acceso.application;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import mx.neology.acceso.application.puerto.Transaccion;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.Placa;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.Vehiculo;
import mx.neology.acceso.domain.excepcion.VehiculoYaRegistradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Alta de un vehículo; una placa provisional (no residente) se reclasifica conservando su id. */
public final class RegistrarVehiculo {

  private static final Logger log = LoggerFactory.getLogger(RegistrarVehiculo.class);

  private final VehiculoRepositorio vehiculos;
  private final Transaccion transaccion;
  private final Clock clock;

  public RegistrarVehiculo(VehiculoRepositorio vehiculos, Transaccion transaccion, Clock clock) {
    this.vehiculos = vehiculos;
    this.transaccion = transaccion;
    this.clock = clock;
  }

  public Vehiculo ejecutar(String placaCruda, TipoVehiculo tipo) {
    Placa placa = Placa.de(placaCruda);
    return transaccion.ejecutar(
        () -> {
          OffsetDateTime ahora = OffsetDateTime.now(clock);
          Optional<Vehiculo> existente = vehiculos.buscarPorPlacaParaActualizar(placa);
          if (existente.isEmpty()) {
            Vehiculo creado = vehiculos.guardar(Vehiculo.nuevo(placa, tipo, ahora));
            log.info("evento=alta placa={} tipo={} id={}", placa, tipo, creado.id());
            return creado;
          }
          Vehiculo actual = existente.get();
          if (!actual.puedeReclasificarseA(tipo)) {
            throw new VehiculoYaRegistradoException(placa.valor());
          }
          Vehiculo reclasificado = vehiculos.guardar(actual.reclasificar(tipo, ahora));
          log.info(
              "evento=reclasificacion placa={} tipo_anterior={} tipo={} id={}",
              placa,
              actual.tipo(),
              tipo,
              reclasificado.id());
          return reclasificado;
        });
  }
}
