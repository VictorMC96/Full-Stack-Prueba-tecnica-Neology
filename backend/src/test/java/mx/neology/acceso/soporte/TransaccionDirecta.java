package mx.neology.acceso.soporte;

import java.util.function.Supplier;
import mx.neology.acceso.application.puerto.Transaccion;

/** Ejecuta la acción sin transacción, para pruebas unitarias de casos de uso. */
public class TransaccionDirecta implements Transaccion {

  @Override
  public <T> T ejecutar(Supplier<T> accion) {
    return accion.get();
  }

  @Override
  public <T> T consultar(Supplier<T> accion) {
    return accion.get();
  }
}
