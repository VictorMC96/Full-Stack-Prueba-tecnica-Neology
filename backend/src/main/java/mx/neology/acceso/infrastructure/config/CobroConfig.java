package mx.neology.acceso.infrastructure.config;

import java.util.List;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.cobro.CalculadoraCobro;
import mx.neology.acceso.domain.cobro.CobroNoResidente;
import mx.neology.acceso.domain.cobro.CobroOficial;
import mx.neology.acceso.domain.cobro.CobroResidente;
import mx.neology.acceso.domain.cobro.EstrategiaCobro;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Una estrategia por tipo de vehículo; añadir un tipo = añadir aquí su bean (su tarifa se lee de
 * {@code acceso.tarifas.<tipo>} sin tocar ninguna otra clase).
 */
@Configuration
public class CobroConfig {

  @Bean
  EstrategiaCobro cobroOficial() {
    return new CobroOficial();
  }

  @Bean
  EstrategiaCobro cobroResidente(TarifasProperties tarifas) {
    return new CobroResidente(tarifas.porMinuto(TipoVehiculo.RESIDENTE));
  }

  @Bean
  EstrategiaCobro cobroNoResidente(TarifasProperties tarifas) {
    return new CobroNoResidente(tarifas.porMinuto(TipoVehiculo.NO_RESIDENTE));
  }

  @Bean
  CalculadoraCobro calculadoraCobro(List<EstrategiaCobro> estrategias) {
    return new CalculadoraCobro(estrategias);
  }
}
