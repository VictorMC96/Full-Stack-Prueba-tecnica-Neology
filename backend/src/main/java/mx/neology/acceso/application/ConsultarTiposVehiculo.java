package mx.neology.acceso.application;

import java.util.List;
import java.util.Map;
import mx.neology.acceso.application.modelo.TipoVehiculoConUso;
import mx.neology.acceso.application.puerto.TipoVehiculoRepositorio;
import mx.neology.acceso.application.puerto.Transaccion;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.CodigoTipo;
import mx.neology.acceso.domain.excepcion.TipoNoEncontradoException;

/** Catálogo de tipos de vehículo con el número de vehículos registrados por tipo. */
public final class ConsultarTiposVehiculo {

  private final TipoVehiculoRepositorio tipos;
  private final VehiculoRepositorio vehiculos;
  private final Transaccion transaccion;

  public ConsultarTiposVehiculo(
      TipoVehiculoRepositorio tipos, VehiculoRepositorio vehiculos, Transaccion transaccion) {
    this.tipos = tipos;
    this.vehiculos = vehiculos;
    this.transaccion = transaccion;
  }

  public List<TipoVehiculoConUso> listar() {
    return transaccion.consultar(
        () -> {
          Map<CodigoTipo, Long> conteo = vehiculos.conteoPorTipo();
          return tipos.listar().stream()
              .map(t -> new TipoVehiculoConUso(t, conteo.getOrDefault(t.codigo(), 0L)))
              .toList();
        });
  }

  public TipoVehiculoConUso buscar(String codigoCrudo) {
    CodigoTipo codigo = CodigoTipo.de(codigoCrudo);
    return transaccion.consultar(
        () ->
            tipos
                .buscar(codigo)
                .map(t -> new TipoVehiculoConUso(t, vehiculos.contarPorTipo(codigo)))
                .orElseThrow(() -> new TipoNoEncontradoException(codigo.valor())));
  }
}
