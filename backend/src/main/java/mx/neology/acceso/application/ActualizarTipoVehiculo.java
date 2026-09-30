package mx.neology.acceso.application;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import mx.neology.acceso.application.modelo.TipoVehiculoConUso;
import mx.neology.acceso.application.puerto.TipoVehiculoRepositorio;
import mx.neology.acceso.application.puerto.Transaccion;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.CodigoTipo;
import mx.neology.acceso.domain.ModeloCobro;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.cobro.CalculadoraCobro;
import mx.neology.acceso.domain.excepcion.TipoIntegradoException;
import mx.neology.acceso.domain.excepcion.TipoNoEncontradoException;
import mx.neology.acceso.domain.excepcion.ValidacionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Cambia nombre, modelo de cobro y tarifa; un tipo integrado conserva su modelo. */
public final class ActualizarTipoVehiculo {

  private static final Logger log = LoggerFactory.getLogger(ActualizarTipoVehiculo.class);

  private final TipoVehiculoRepositorio tipos;
  private final VehiculoRepositorio vehiculos;
  private final CalculadoraCobro calculadora;
  private final Transaccion transaccion;
  private final Clock clock;

  public ActualizarTipoVehiculo(
      TipoVehiculoRepositorio tipos,
      VehiculoRepositorio vehiculos,
      CalculadoraCobro calculadora,
      Transaccion transaccion,
      Clock clock) {
    this.tipos = tipos;
    this.vehiculos = vehiculos;
    this.calculadora = calculadora;
    this.transaccion = transaccion;
    this.clock = clock;
  }

  public TipoVehiculoConUso ejecutar(
      String codigoCrudo, String nombre, ModeloCobro modelo, BigDecimal tarifaPorMinuto) {
    CodigoTipo codigo = CodigoTipo.de(codigoCrudo);
    if (modelo == null) {
      throw new ValidacionException("modeloCobro", "El modelo de cobro es obligatorio");
    }
    return transaccion.ejecutar(
        () -> {
          TipoVehiculo actual =
              tipos.buscar(codigo).orElseThrow(() -> new TipoNoEncontradoException(codigo.valor()));
          if (actual.integrado() && actual.modeloCobro() != modelo) {
            throw new TipoIntegradoException(codigo.valor(), "admite cambiar su modelo de cobro");
          }
          calculadora.validarTarifa(modelo, tarifaPorMinuto);
          TipoVehiculo guardado =
              tipos.guardar(
                  actual.actualizar(nombre, modelo, tarifaPorMinuto, OffsetDateTime.now(clock)));
          log.info(
              "evento=tipo_actualizado codigo={} modelo={} tarifa={}",
              codigo,
              guardado.modeloCobro(),
              guardado.tarifaPorMinuto());
          return new TipoVehiculoConUso(guardado, vehiculos.contarPorTipo(codigo));
        });
  }
}
