package mx.neology.acceso.application;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import mx.neology.acceso.domain.Periodo;
import mx.neology.acceso.domain.Placa;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.Vehiculo;
import mx.neology.acceso.domain.cobro.CalculadoraCobro;
import mx.neology.acceso.domain.cobro.CobroNoResidente;
import mx.neology.acceso.domain.cobro.CobroOficial;
import mx.neology.acceso.domain.cobro.CobroResidente;

final class Fixtures {

  static final ZoneId ZONA = ZoneId.of("America/Mexico_City");
  static final Instant INSTANTE = Instant.parse("2026-09-15T18:00:00Z");
  static final Clock RELOJ = Clock.fixed(INSTANTE, ZONA);
  static final OffsetDateTime AHORA = OffsetDateTime.now(RELOJ);
  static final OffsetDateTime AYER = AHORA.minusDays(1);

  private Fixtures() {}

  static CalculadoraCobro calculadora() {
    return new CalculadoraCobro(
        List.of(
            new CobroOficial(),
            new CobroResidente(new BigDecimal("0.05")),
            new CobroNoResidente(new BigDecimal("0.50"))));
  }

  static Vehiculo vehiculo(String placa, TipoVehiculo tipo) {
    return new Vehiculo(UUID.randomUUID(), Placa.de(placa), tipo, null, AYER, AYER);
  }

  static Periodo periodoActivo() {
    return new Periodo(UUID.randomUUID(), AYER, null, Boolean.TRUE, AYER, AYER);
  }
}
