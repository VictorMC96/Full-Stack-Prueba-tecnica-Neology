package mx.neology.acceso.web;

import mx.neology.acceso.application.modelo.DetalleVehiculo;
import mx.neology.acceso.application.modelo.EstanciaDeVehiculo;
import mx.neology.acceso.application.modelo.InformePagos;
import mx.neology.acceso.domain.Acumulado;
import mx.neology.acceso.domain.Periodo;
import mx.neology.acceso.domain.Vehiculo;

/** Conversión de modelos de aplicación a records de respuesta HTTP. */
final class Respuestas {

  private Respuestas() {}

  static VehiculoResponse vehiculo(Vehiculo v) {
    return new VehiculoResponse(v.id(), v.placa().valor(), v.tipo(), v.dentro(), v.createdAt());
  }

  static VehiculoResumen resumen(Vehiculo v) {
    return new VehiculoResumen(v.placa().valor(), v.tipo(), v.dentro(), v.createdAt());
  }

  static EstanciaResponse estancia(EstanciaDeVehiculo e) {
    return new EstanciaResponse(
        e.estancia().id(),
        e.placa().valor(),
        e.tipo(),
        e.estancia().entrada(),
        e.estancia().salida(),
        e.estancia().minutos(),
        e.estancia().importe(),
        e.momentoCobro(),
        e.estancia().periodoId());
  }

  static AcumuladoResponse acumulado(Acumulado a) {
    return new AcumuladoResponse(a.minutos(), a.importe());
  }

  static VehiculoDetalleResponse detalle(DetalleVehiculo d) {
    Vehiculo v = d.vehiculo();
    return new VehiculoDetalleResponse(
        v.id(),
        v.placa().valor(),
        v.tipo(),
        v.dentro(),
        d.estanciaAbierta().map(Respuestas::estancia).orElse(null),
        d.acumuladoPeriodo().map(Respuestas::acumulado).orElse(null),
        v.createdAt());
  }

  static PeriodoResponse periodo(Periodo p) {
    return new PeriodoResponse(p.id(), p.inicio(), p.fin());
  }

  static InformePagosResponse informe(InformePagos i) {
    return new InformePagosResponse(
        periodo(i.periodo()),
        i.filas().stream()
            .map(f -> new InformePagosResponse.Fila(f.placa(), f.minutos(), f.importe()))
            .toList(),
        i.totalMinutos(),
        i.totalImporte());
  }
}
