package mx.neology.acceso.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import mx.neology.acceso.application.modelo.FilaInforme;
import mx.neology.acceso.application.modelo.InformePagos;
import mx.neology.acceso.application.puerto.PeriodoRepositorio;
import mx.neology.acceso.application.puerto.Transaccion;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.Periodo;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.cobro.CalculadoraCobro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Informe del periodo activo con los vehículos cuya estrategia cobra en el informe mensual. */
public final class GenerarInformePagos {

  private static final Logger log = LoggerFactory.getLogger(GenerarInformePagos.class);

  private final VehiculoRepositorio vehiculos;
  private final PeriodoRepositorio periodos;
  private final CalculadoraCobro calculadora;
  private final Transaccion transaccion;

  public GenerarInformePagos(
      VehiculoRepositorio vehiculos,
      PeriodoRepositorio periodos,
      CalculadoraCobro calculadora,
      Transaccion transaccion) {
    this.vehiculos = vehiculos;
    this.periodos = periodos;
    this.calculadora = calculadora;
    this.transaccion = transaccion;
  }

  public InformePagos ejecutar() {
    return transaccion.consultar(
        () -> {
          Periodo periodo = periodos.activo();
          Set<TipoVehiculo> tipos = calculadora.tiposConMomento(MomentoCobro.INFORME_MENSUAL);
          List<FilaInforme> filas =
              vehiculos.acumuladosPorTipo(tipos, periodo.id()).stream()
                  .map(GenerarInformePagos::normalizar)
                  .sorted(Comparator.comparing(FilaInforme::placa))
                  .toList();
          long totalMinutos = filas.stream().mapToLong(FilaInforme::minutos).sum();
          BigDecimal totalImporte =
              filas.stream()
                  .map(FilaInforme::importe)
                  .reduce(BigDecimal.ZERO, BigDecimal::add)
                  .setScale(2, RoundingMode.HALF_UP);
          log.info(
              "evento=informe_pagos periodo={} filas={} total_minutos={} total_importe={}",
              periodo.id(),
              filas.size(),
              totalMinutos,
              totalImporte);
          return new InformePagos(periodo, filas, totalMinutos, totalImporte);
        });
  }

  private static FilaInforme normalizar(FilaInforme fila) {
    BigDecimal importe = fila.importe() == null ? BigDecimal.ZERO : fila.importe();
    return new FilaInforme(fila.placa(), fila.minutos(), importe.setScale(2, RoundingMode.HALF_UP));
  }
}
