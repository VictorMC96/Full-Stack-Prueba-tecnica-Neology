package mx.neology.acceso.application;

import java.time.Clock;
import java.time.OffsetDateTime;
import mx.neology.acceso.application.modelo.ResultadoIniciarMes;
import mx.neology.acceso.application.puerto.PeriodoRepositorio;
import mx.neology.acceso.application.puerto.Transaccion;
import mx.neology.acceso.domain.Periodo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Cierra el periodo activo y abre uno nuevo; nada se borra, todo queda archivado. */
public final class IniciarMes {

  private static final Logger log = LoggerFactory.getLogger(IniciarMes.class);

  private final PeriodoRepositorio periodos;
  private final Transaccion transaccion;
  private final Clock clock;

  public IniciarMes(PeriodoRepositorio periodos, Transaccion transaccion, Clock clock) {
    this.periodos = periodos;
    this.transaccion = transaccion;
    this.clock = clock;
  }

  public ResultadoIniciarMes ejecutar() {
    return transaccion.ejecutar(
        () -> {
          OffsetDateTime ahora = OffsetDateTime.now(clock);
          Periodo anterior = periodos.cerrar(periodos.activo().cerrar(ahora));
          Periodo actual = periodos.abrir(Periodo.abrir(ahora));
          log.info(
              "evento=mes_iniciado periodo_anterior={} periodo_actual={}",
              anterior.id(),
              actual.id());
          return new ResultadoIniciarMes(anterior, actual);
        });
  }
}
