package mx.neology.acceso.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Objects;
import mx.neology.acceso.domain.excepcion.ValidacionException;

/**
 * Tipo de vehículo administrable: código, nombre, modelo de cobro y tarifa. El tipo provisional es
 * el que recibe una placa que entra sin alta previa; los integrados (los del enunciado) no pueden
 * eliminarse ni cambiar de modelo de cobro.
 */
public record TipoVehiculo(
    CodigoTipo codigo,
    String nombre,
    ModeloCobro modeloCobro,
    BigDecimal tarifaPorMinuto,
    boolean provisional,
    boolean integrado,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {

  public TipoVehiculo {
    Objects.requireNonNull(codigo, "codigo");
    if (nombre == null || nombre.isBlank() || nombre.trim().length() < 2 || nombre.length() > 40) {
      throw new ValidacionException("nombre", "El nombre debe tener entre 2 y 40 caracteres");
    }
    nombre = nombre.trim();
    if (modeloCobro == null) {
      throw new ValidacionException("modeloCobro", "El modelo de cobro es obligatorio");
    }
    if (tarifaPorMinuto == null || tarifaPorMinuto.signum() < 0) {
      throw new ValidacionException(
          "tarifaPorMinuto", "La tarifa por minuto debe ser mayor o igual que 0.00");
    }
    if (tarifaPorMinuto.stripTrailingZeros().scale() > 2) {
      throw new ValidacionException(
          "tarifaPorMinuto", "La tarifa por minuto admite como máximo 2 decimales");
    }
    tarifaPorMinuto = tarifaPorMinuto.setScale(2, RoundingMode.HALF_UP);
  }

  /** Tipo creado desde la API: nunca provisional ni integrado. */
  public static TipoVehiculo nuevo(
      CodigoTipo codigo,
      String nombre,
      ModeloCobro modeloCobro,
      BigDecimal tarifaPorMinuto,
      OffsetDateTime ahora) {
    return new TipoVehiculo(
        codigo, nombre, modeloCobro, tarifaPorMinuto, false, false, ahora, ahora);
  }

  public TipoVehiculo actualizar(
      String nuevoNombre, ModeloCobro nuevoModelo, BigDecimal nuevaTarifa, OffsetDateTime ahora) {
    return new TipoVehiculo(
        codigo, nuevoNombre, nuevoModelo, nuevaTarifa, provisional, integrado, createdAt, ahora);
  }

  /** Solo un vehículo de tipo provisional puede reclasificarse, y solo a un tipo definitivo. */
  public boolean permiteReclasificarA(TipoVehiculo nuevo) {
    return provisional && !nuevo.provisional();
  }
}
