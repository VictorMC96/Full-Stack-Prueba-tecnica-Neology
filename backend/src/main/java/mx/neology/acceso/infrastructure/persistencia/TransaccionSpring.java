package mx.neology.acceso.infrastructure.persistencia;

import java.util.function.Supplier;
import mx.neology.acceso.application.puerto.Transaccion;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/** Implementa la frontera transaccional con {@link TransactionTemplate}. */
@Component
public class TransaccionSpring implements Transaccion {

  private final TransactionTemplate escritura;
  private final TransactionTemplate lectura;

  public TransaccionSpring(PlatformTransactionManager gestor) {
    this.escritura = new TransactionTemplate(gestor);
    this.lectura = new TransactionTemplate(gestor);
    this.lectura.setReadOnly(true);
  }

  @Override
  public <T> T ejecutar(Supplier<T> accion) {
    return escritura.execute(estado -> accion.get());
  }

  @Override
  public <T> T consultar(Supplier<T> accion) {
    return lectura.execute(estado -> accion.get());
  }
}
