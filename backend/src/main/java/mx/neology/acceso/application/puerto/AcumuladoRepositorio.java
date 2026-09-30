package mx.neology.acceso.application.puerto;

import java.util.Optional;
import java.util.UUID;
import mx.neology.acceso.domain.Acumulado;

public interface AcumuladoRepositorio {

  Optional<Acumulado> buscar(UUID vehiculoId, UUID periodoId);

  Acumulado guardar(Acumulado acumulado);
}
