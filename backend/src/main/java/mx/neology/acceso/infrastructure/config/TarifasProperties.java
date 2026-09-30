package mx.neology.acceso.infrastructure.config;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** Tarifas por minuto leídas de {@code acceso.tarifas.*} en {@code application.yml}. */
@ConfigurationProperties(prefix = "acceso.tarifas")
@Validated
public record TarifasProperties(
    @NotNull @DecimalMin("0.00") BigDecimal residente,
    @NotNull @DecimalMin("0.00") BigDecimal noResidente) {}
