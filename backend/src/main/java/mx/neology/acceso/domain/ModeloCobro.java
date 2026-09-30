package mx.neology.acceso.domain;

/**
 * Modelos de cobro disponibles. Cada constante tiene una {@code EstrategiaCobro}; añadir un modelo
 * es añadir una constante, una estrategia y su bean.
 */
public enum ModeloCobro {
  SIN_COBRO,
  POR_MINUTO_AL_SALIR,
  POR_MINUTO_ACUMULADO
}
