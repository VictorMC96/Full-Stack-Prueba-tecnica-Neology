package mx.neology.acceso.application.puerto;

import java.util.function.Supplier;

/** Frontera transaccional de los casos de uso, sin depender de Spring. */
public interface Transaccion {

  /** Ejecuta la acción en una transacción de escritura. */
  <T> T ejecutar(Supplier<T> accion);

  /** Ejecuta la acción en una transacción de solo lectura. */
  <T> T consultar(Supplier<T> accion);
}
