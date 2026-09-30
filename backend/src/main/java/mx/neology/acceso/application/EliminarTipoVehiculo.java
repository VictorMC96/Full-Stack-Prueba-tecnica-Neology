package mx.neology.acceso.application;

import mx.neology.acceso.application.puerto.TipoVehiculoRepositorio;
import mx.neology.acceso.application.puerto.Transaccion;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.CodigoTipo;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.excepcion.TipoEnUsoException;
import mx.neology.acceso.domain.excepcion.TipoIntegradoException;
import mx.neology.acceso.domain.excepcion.TipoNoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Elimina un tipo creado desde la API que no tenga vehículos registrados. */
public final class EliminarTipoVehiculo {

  private static final Logger log = LoggerFactory.getLogger(EliminarTipoVehiculo.class);

  private final TipoVehiculoRepositorio tipos;
  private final VehiculoRepositorio vehiculos;
  private final Transaccion transaccion;

  public EliminarTipoVehiculo(
      TipoVehiculoRepositorio tipos, VehiculoRepositorio vehiculos, Transaccion transaccion) {
    this.tipos = tipos;
    this.vehiculos = vehiculos;
    this.transaccion = transaccion;
  }

  public void ejecutar(String codigoCrudo) {
    CodigoTipo codigo = CodigoTipo.de(codigoCrudo);
    transaccion.ejecutar(
        () -> {
          TipoVehiculo actual =
              tipos.buscar(codigo).orElseThrow(() -> new TipoNoEncontradoException(codigo.valor()));
          if (actual.integrado()) {
            throw new TipoIntegradoException(codigo.valor(), "puede eliminarse");
          }
          long enUso = vehiculos.contarPorTipo(codigo);
          if (enUso > 0) {
            throw new TipoEnUsoException(codigo.valor(), enUso);
          }
          tipos.eliminar(codigo);
          log.info("evento=tipo_eliminado codigo={}", codigo);
          return null;
        });
  }
}
