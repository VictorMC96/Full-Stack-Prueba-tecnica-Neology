package mx.neology.acceso.application.puerto;

import mx.neology.acceso.domain.Periodo;

public interface PeriodoRepositorio {

  Periodo activo();

  /** Persiste el cierre y lo confirma en la base antes de que se abra el siguiente. */
  Periodo cerrar(Periodo periodo);

  Periodo abrir(Periodo periodo);
}
