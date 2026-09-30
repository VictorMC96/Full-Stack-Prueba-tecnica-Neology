package mx.neology.acceso.domain.cobro;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import mx.neology.acceso.domain.ModeloCobro;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.TipoVehiculo;

/**
 * Registro de estrategias indexado por modelo de cobro. Los tipos de vehículo son datos (tabla
 * {@code tipos_vehiculo}) que eligen un modelo; aquí no hay {@code if} ni {@code switch} por tipo
 * ni por modelo.
 */
public final class CalculadoraCobro {

  private final Map<ModeloCobro, EstrategiaCobro> estrategias;

  public CalculadoraCobro(Collection<EstrategiaCobro> lista) {
    EnumMap<ModeloCobro, EstrategiaCobro> mapa = new EnumMap<>(ModeloCobro.class);
    for (EstrategiaCobro estrategia : lista) {
      mapa.put(estrategia.modelo(), estrategia);
    }
    for (ModeloCobro modelo : ModeloCobro.values()) {
      if (!mapa.containsKey(modelo)) {
        throw new IllegalStateException("Falta la estrategia de cobro para el modelo " + modelo);
      }
    }
    this.estrategias = Collections.unmodifiableMap(mapa);
  }

  public BigDecimal calcular(TipoVehiculo tipo, long minutos) {
    return estrategia(tipo.modeloCobro()).calcular(tipo.tarifaPorMinuto(), minutos);
  }

  public MomentoCobro momento(TipoVehiculo tipo) {
    return momento(tipo.modeloCobro());
  }

  public MomentoCobro momento(ModeloCobro modelo) {
    return estrategia(modelo).momento();
  }

  /** Valida la tarifa de un tipo según lo que admite su modelo de cobro. */
  public void validarTarifa(ModeloCobro modelo, BigDecimal tarifaPorMinuto) {
    estrategia(modelo).validarTarifa(tarifaPorMinuto);
  }

  /** Modelos cuya estrategia cobra en el momento indicado (p. ej. los del informe mensual). */
  public Set<ModeloCobro> modelosConMomento(MomentoCobro momento) {
    EnumSet<ModeloCobro> modelos = EnumSet.noneOf(ModeloCobro.class);
    estrategias.values().stream()
        .filter(e -> e.momento() == momento)
        .forEach(e -> modelos.add(e.modelo()));
    return modelos;
  }

  private EstrategiaCobro estrategia(ModeloCobro modelo) {
    EstrategiaCobro estrategia = modelo == null ? null : estrategias.get(modelo);
    if (estrategia == null) {
      throw new IllegalStateException("No hay estrategia de cobro para el modelo " + modelo);
    }
    return estrategia;
  }
}
