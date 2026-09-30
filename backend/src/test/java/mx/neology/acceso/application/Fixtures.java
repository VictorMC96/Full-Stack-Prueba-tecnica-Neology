package mx.neology.acceso.application;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import mx.neology.acceso.application.puerto.TipoVehiculoRepositorio;
import mx.neology.acceso.domain.CodigoTipo;
import mx.neology.acceso.domain.ModeloCobro;
import mx.neology.acceso.domain.Periodo;
import mx.neology.acceso.domain.Placa;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.Vehiculo;
import mx.neology.acceso.domain.cobro.CalculadoraCobro;
import mx.neology.acceso.domain.cobro.PorMinutoAcumulado;
import mx.neology.acceso.domain.cobro.PorMinutoAlSalir;
import mx.neology.acceso.domain.cobro.SinCobro;
import org.mockito.Mockito;

/** Datos de prueba: los tres tipos base del catálogo y un reloj fijo. */
final class Fixtures {

  static final ZoneId ZONA = ZoneId.of("America/Mexico_City");
  static final Instant INSTANTE = Instant.parse("2026-09-15T18:00:00Z");
  static final Clock RELOJ = Clock.fixed(INSTANTE, ZONA);
  static final OffsetDateTime AHORA = OffsetDateTime.now(RELOJ);
  static final OffsetDateTime AYER = AHORA.minusDays(1);

  static final TipoVehiculo OFICIAL =
      tipo("OFICIAL", "Oficial", ModeloCobro.SIN_COBRO, "0.00", false, true);
  static final TipoVehiculo RESIDENTE =
      tipo("RESIDENTE", "Residente", ModeloCobro.POR_MINUTO_ACUMULADO, "0.05", false, true);
  static final TipoVehiculo NO_RESIDENTE =
      tipo("NO_RESIDENTE", "No residente", ModeloCobro.POR_MINUTO_AL_SALIR, "0.50", true, true);
  static final TipoVehiculo VISITANTE =
      tipo("VISITANTE", "Visitante", ModeloCobro.POR_MINUTO_AL_SALIR, "0.30", false, false);

  private Fixtures() {}

  static TipoVehiculo tipo(
      String codigo,
      String nombre,
      ModeloCobro modelo,
      String tarifa,
      boolean provisional,
      boolean integrado) {
    return new TipoVehiculo(
        new CodigoTipo(codigo),
        nombre,
        modelo,
        new BigDecimal(tarifa),
        provisional,
        integrado,
        AYER,
        AYER);
  }

  static CalculadoraCobro calculadora() {
    return new CalculadoraCobro(
        List.of(new SinCobro(), new PorMinutoAlSalir(), new PorMinutoAcumulado()));
  }

  /** Repositorio simulado que conoce los tres tipos base (y VISITANTE). */
  static TipoVehiculoRepositorio catalogo() {
    TipoVehiculoRepositorio tipos = Mockito.mock(TipoVehiculoRepositorio.class);
    List<TipoVehiculo> todos = List.of(NO_RESIDENTE, OFICIAL, RESIDENTE, VISITANTE);
    Mockito.lenient().when(tipos.listar()).thenReturn(todos);
    Mockito.lenient().when(tipos.provisional()).thenReturn(NO_RESIDENTE);
    for (TipoVehiculo t : todos) {
      Mockito.lenient().when(tipos.buscar(t.codigo())).thenReturn(Optional.of(t));
    }
    Mockito.lenient().when(tipos.buscar(new CodigoTipo("ZZZ"))).thenReturn(Optional.empty());
    return tipos;
  }

  static Vehiculo vehiculo(String placa, TipoVehiculo tipo) {
    return new Vehiculo(UUID.randomUUID(), Placa.de(placa), tipo.codigo(), null, AYER, AYER);
  }

  static Periodo periodoActivo() {
    return new Periodo(UUID.randomUUID(), AYER, null, Boolean.TRUE, AYER, AYER);
  }
}
