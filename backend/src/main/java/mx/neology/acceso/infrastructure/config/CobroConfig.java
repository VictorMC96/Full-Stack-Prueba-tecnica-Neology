package mx.neology.acceso.infrastructure.config;

import java.util.List;
import mx.neology.acceso.domain.cobro.CalculadoraCobro;
import mx.neology.acceso.domain.cobro.EstrategiaCobro;
import mx.neology.acceso.domain.cobro.PorMinutoAcumulado;
import mx.neology.acceso.domain.cobro.PorMinutoAlSalir;
import mx.neology.acceso.domain.cobro.SinCobro;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Una estrategia por modelo de cobro. Los tipos de vehículo (y sus tarifas) son filas de {@code
 * tipos_vehiculo}; añadir un modelo de cobro nuevo = constante en {@code ModeloCobro} + clase de
 * estrategia + su bean aquí. {@code CalculadoraCobro} falla al arrancar si falta alguno.
 */
@Configuration
public class CobroConfig {

  @Bean
  EstrategiaCobro sinCobro() {
    return new SinCobro();
  }

  @Bean
  EstrategiaCobro porMinutoAlSalir() {
    return new PorMinutoAlSalir();
  }

  @Bean
  EstrategiaCobro porMinutoAcumulado() {
    return new PorMinutoAcumulado();
  }

  @Bean
  CalculadoraCobro calculadoraCobro(List<EstrategiaCobro> estrategias) {
    return new CalculadoraCobro(estrategias);
  }
}
