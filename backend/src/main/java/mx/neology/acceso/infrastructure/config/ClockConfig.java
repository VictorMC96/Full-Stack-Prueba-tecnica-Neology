package mx.neology.acceso.infrastructure.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Único reloj del sistema; toda fecha sale de {@code OffsetDateTime.now(clock)}. */
@Configuration
public class ClockConfig {

  @Bean
  Clock clock(@Value("${acceso.zona-horaria}") String zona) {
    return Clock.system(ZoneId.of(zona));
  }
}
