package mx.neology.acceso.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mx.neology.acceso.application.modelo.FilaInforme;
import mx.neology.acceso.application.modelo.InformePagos;
import mx.neology.acceso.application.puerto.PeriodoRepositorio;
import mx.neology.acceso.application.puerto.TipoVehiculoRepositorio;
import mx.neology.acceso.application.puerto.Transaccion;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.CodigoTipo;
import mx.neology.acceso.domain.ModeloCobro;
import mx.neology.acceso.domain.MomentoCobro;
import mx.neology.acceso.domain.Periodo;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.cobro.CalculadoraCobro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Informe del periodo activo con los vehículos cuyo modelo de cobro se liquida en el informe. */
public final class GenerarInformePagos {

  private static final Logger log = LoggerFactory.getLogger(GenerarInformePagos.class);

  private final VehiculoRepositorio vehiculos;
  private final PeriodoRepositorio periodos;
  private final TipoVehiculoRepositorio tipos;
  private final CalculadoraCobro calculadora;
  private final Transaccion transaccion;

  public GenerarInformePagos(
      VehiculoRepositorio vehiculos,
      PeriodoRepositorio periodos,
      TipoVehiculoRepositorio tipos,
      CalculadoraCobro calculadora,
      Transaccion transaccion) {
    this.vehiculos = vehiculos;
    this.periodos = periodos;
    this.tipos = tipos;
    this.calculadora = calculadora;
    this.transaccion = transaccion;
  }

  public InformePagos ejecutar() {
    return transaccion.consultar(
        () -> {
          Periodo periodo = periodos.activo();
          Set<ModeloCobro> modelos = calculadora.modelosConMomento(MomentoCobro.INFORME_MENSUAL);
          Set<CodigoTipo> codigos =
              tipos.listar().stream()
                  .filter(t -> modelos.contains(t.modeloCobro()))
                  .map(TipoVehiculo::codigo)
                  .collect(Collectors.toSet());
          List<FilaInforme> filas =
              vehiculos.acumuladosPorTipo(codigos, periodo.id()).stream()
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
              "evento=informe_pagos periodo={} tipos={} filas={} total_minutos={} total_importe={}",
              periodo.id(),
              codigos,
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
