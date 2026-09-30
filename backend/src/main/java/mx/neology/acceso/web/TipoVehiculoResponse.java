package mx.neology.acceso.web;

import java.math.BigDecimal;
import mx.neology.acceso.application.modelo.TipoVehiculoConUso;
import mx.neology.acceso.domain.ModeloCobro;
import mx.neology.acceso.domain.TipoVehiculo;

public record TipoVehiculoResponse(
    String codigo,
    String nombre,
    ModeloCobro modeloCobro,
    BigDecimal tarifaPorMinuto,
    boolean provisional,
    boolean integrado,
    long vehiculos) {

  static TipoVehiculoResponse de(TipoVehiculo t, long vehiculos) {
    return new TipoVehiculoResponse(
        t.codigo().valor(),
        t.nombre(),
        t.modeloCobro(),
        t.tarifaPorMinuto(),
        t.provisional(),
        t.integrado(),
        vehiculos);
  }

  static TipoVehiculoResponse de(TipoVehiculoConUso t) {
    return de(t.tipo(), t.vehiculos());
  }
}
