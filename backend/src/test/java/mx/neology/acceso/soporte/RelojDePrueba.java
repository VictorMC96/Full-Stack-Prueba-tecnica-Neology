package mx.neology.acceso.soporte;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/** Reloj ajustable para las pruebas de integración, en la zona America/Mexico_City. */
public class RelojDePrueba extends Clock {

  public static final ZoneId ZONA = ZoneId.of("America/Mexico_City");

  private Instant instante = Instant.parse("2026-09-15T18:00:00Z");

  public void fijar(Instant nuevo) {
    this.instante = nuevo;
  }

  public void avanzar(Duration duracion) {
    this.instante = instante.plus(duracion);
  }

  @Override
  public ZoneId getZone() {
    return ZONA;
  }

  @Override
  public Clock withZone(ZoneId zone) {
    return this;
  }

  @Override
  public Instant instant() {
    return instante;
  }

  @TestConfiguration
  public static class Configuracion {

    @Bean
    @Primary
    RelojDePrueba relojDePrueba() {
      return new RelojDePrueba();
    }
  }
}
