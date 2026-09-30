package mx.neology.acceso.domain.cobro;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.TipoVehiculo;

/**
 * Registro de estrategias indexado por tipo. Añadir un tipo de vehículo es añadir una constante al
 * enum y una estrategia; aquí no hay {@code if} ni {@code switch} sobre el tipo.
 */
public final class CalculadoraCobro {

  private final Map<TipoVehiculo, EstrategiaCobro> estrategias;

  public CalculadoraCobro(Collection<EstrategiaCobro> lista) {
    EnumMap<TipoVehiculo, EstrategiaCobro> mapa = new EnumMap<>(TipoVehiculo.class);
    for (EstrategiaCobro estrategia : lista) {
      mapa.put(estrategia.tipo(), estrategia);
    }
    for (TipoVehiculo tipo : TipoVehiculo.values()) {
      if (!mapa.containsKey(tipo)) {
        throw new IllegalStateException("Falta la estrategia de cobro para el tipo " + tipo);
      }
    }
    this.estrategias = Collections.unmodifiableMap(mapa);
  }

  public BigDecimal calcular(TipoVehiculo tipo, long minutos) {
    return estrategias.get(tipo).calcular(minutos);
  }

  public MomentoCobro momento(TipoVehiculo tipo) {
    return estrategias.get(tipo).momento();
  }

  /** Tipos cuya estrategia cobra en el momento indicado (p. ej. los del informe mensual). */
  public Set<TipoVehiculo> tiposConMomento(MomentoCobro momento) {
    EnumSet<TipoVehiculo> tipos = EnumSet.noneOf(TipoVehiculo.class);
    estrategias.values().stream()
        .filter(e -> e.momento() == momento)
        .forEach(e -> tipos.add(e.tipo()));
    return tipos;
  }
}
