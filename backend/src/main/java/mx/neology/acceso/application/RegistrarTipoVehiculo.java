package mx.neology.acceso.application;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import mx.neology.acceso.application.puerto.TipoVehiculoRepositorio;
import mx.neology.acceso.application.puerto.Transaccion;
import mx.neology.acceso.domain.CodigoTipo;
import mx.neology.acceso.domain.ModeloCobro;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.cobro.CalculadoraCobro;
import mx.neology.acceso.domain.excepcion.TipoYaRegistradoException;
import mx.neology.acceso.domain.excepcion.ValidacionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Crea un tipo de vehículo desde la API. Los tipos creados nunca son provisionales ni integrados,
 * así que el catálogo conserva exactamente un tipo provisional.
 */
public final class RegistrarTipoVehiculo {

  private static final Logger log = LoggerFactory.getLogger(RegistrarTipoVehiculo.class);

  private final TipoVehiculoRepositorio tipos;
  private final CalculadoraCobro calculadora;
  private final Transaccion transaccion;
  private final Clock clock;

  public RegistrarTipoVehiculo(
      TipoVehiculoRepositorio tipos,
      CalculadoraCobro calculadora,
      Transaccion transaccion,
      Clock clock) {
    this.tipos = tipos;
    this.calculadora = calculadora;
    this.transaccion = transaccion;
    this.clock = clock;
  }

  public TipoVehiculo ejecutar(
      String codigoCrudo, String nombre, ModeloCobro modelo, BigDecimal tarifaPorMinuto) {
    CodigoTipo codigo = CodigoTipo.de(codigoCrudo);
    if (modelo == null) {
      throw new ValidacionException("modeloCobro", "El modelo de cobro es obligatorio");
    }
    calculadora.validarTarifa(modelo, tarifaPorMinuto);
    TipoVehiculo candidato =
        TipoVehiculo.nuevo(codigo, nombre, modelo, tarifaPorMinuto, OffsetDateTime.now(clock));
    return transaccion.ejecutar(
        () -> {
          if (tipos.buscar(codigo).isPresent()) {
            throw new TipoYaRegistradoException(codigo.valor());
          }
          TipoVehiculo creado = tipos.guardar(candidato);
          log.info(
              "evento=tipo_creado codigo={} modelo={} tarifa={}",
              creado.codigo(),
              creado.modeloCobro(),
              creado.tarifaPorMinuto());
          return creado;
        });
  }
}
