package mx.neology.acceso.application.puerto;

import java.util.Optional;
import java.util.UUID;
import mx.neology.acceso.application.modelo.ConsultaPagina;
import mx.neology.acceso.application.modelo.Pagina;
import mx.neology.acceso.domain.Estancia;

public interface EstanciaRepositorio {

  Estancia guardar(Estancia estancia);

  Optional<Estancia> buscarPorId(UUID id);

  /** Estancias de un vehículo, las más recientes primero. */
  Pagina<Estancia> listarPorVehiculo(UUID vehiculoId, ConsultaPagina consulta);
}
