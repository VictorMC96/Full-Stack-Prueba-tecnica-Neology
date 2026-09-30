package mx.neology.acceso.infrastructure.config;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import mx.neology.acceso.domain.TipoVehiculo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Tarifas por minuto leídas de {@code acceso.tarifas.<tipo>} en {@code application.yml}. La clave
 * es el nombre del tipo en minúsculas y con guiones ({@code no-residente} para {@code
 * NO_RESIDENTE}), de modo que agregar un tipo de vehículo no obliga a tocar esta clase.
 */
@ConfigurationProperties(prefix = "acceso")
@Validated
public record TarifasProperties(
    @NotEmpty Map<String, @NotNull @DecimalMin("0.00") BigDecimal> tarifas) {

  /** Tarifa por minuto del tipo; falla al arrancar, con la clave exacta, si no está configurada. */
  public BigDecimal porMinuto(TipoVehiculo tipo) {
    String clave = clave(tipo);
    BigDecimal tarifa = tarifas.get(clave);
    if (tarifa == null) {
      throw new IllegalStateException(
          "Falta la tarifa acceso.tarifas." + clave + " para el tipo de vehículo " + tipo);
    }
    return tarifa;
  }

  static String clave(TipoVehiculo tipo) {
    return tipo.name().toLowerCase(Locale.ROOT).replace('_', '-');
  }
}
