package mx.neology.acceso.application;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import mx.neology.acceso.application.modelo.VehiculoConTipo;
import mx.neology.acceso.application.puerto.TipoVehiculoRepositorio;
import mx.neology.acceso.application.puerto.Transaccion;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.CodigoTipo;
import mx.neology.acceso.domain.Placa;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.Vehiculo;
import mx.neology.acceso.domain.excepcion.TipoNoEncontradoException;
import mx.neology.acceso.domain.excepcion.VehiculoYaRegistradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Alta de un vehículo con un tipo del catálogo; una placa con tipo provisional se reclasifica
 * conservando su id.
 */
public final class RegistrarVehiculo {

  private static final Logger log = LoggerFactory.getLogger(RegistrarVehiculo.class);

  private final VehiculoRepositorio vehiculos;
  private final TipoVehiculoRepositorio tipos;
  private final Transaccion transaccion;
  private final Clock clock;

  public RegistrarVehiculo(
      VehiculoRepositorio vehiculos,
      TipoVehiculoRepositorio tipos,
      Transaccion transaccion,
      Clock clock) {
    this.vehiculos = vehiculos;
    this.tipos = tipos;
    this.transaccion = transaccion;
    this.clock = clock;
  }

  public VehiculoConTipo ejecutar(String placaCruda, String codigoTipoCrudo) {
    Placa placa = Placa.de(placaCruda);
    CodigoTipo codigo = CodigoTipo.de(codigoTipoCrudo);
    return transaccion.ejecutar(
        () -> {
          OffsetDateTime ahora = OffsetDateTime.now(clock);
          TipoVehiculo tipoNuevo =
              tipos.buscar(codigo).orElseThrow(() -> new TipoNoEncontradoException(codigo.valor()));
          Optional<Vehiculo> existente = vehiculos.buscarPorPlacaParaActualizar(placa);
          if (existente.isEmpty()) {
            Vehiculo creado = vehiculos.guardar(Vehiculo.nuevo(placa, codigo, ahora));
            log.info("evento=alta placa={} tipo={} id={}", placa, codigo, creado.id());
            return new VehiculoConTipo(creado, tipoNuevo);
          }
          Vehiculo actual = existente.get();
          TipoVehiculo tipoActual = Tipos.de(tipos, actual);
          if (!tipoActual.permiteReclasificarA(tipoNuevo)) {
            throw new VehiculoYaRegistradoException(placa.valor());
          }
          Vehiculo reclasificado = vehiculos.guardar(actual.reclasificar(codigo, ahora));
          log.info(
              "evento=reclasificacion placa={} tipo_anterior={} tipo={} id={}",
              placa,
              actual.tipo(),
              codigo,
              reclasificado.id());
          return new VehiculoConTipo(reclasificado, tipoNuevo);
        });
  }
}
