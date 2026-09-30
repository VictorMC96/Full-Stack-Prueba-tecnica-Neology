package mx.neology.acceso.application.puerto;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import mx.neology.acceso.application.modelo.ConsultaPagina;
import mx.neology.acceso.application.modelo.FilaInforme;
import mx.neology.acceso.application.modelo.Pagina;
import mx.neology.acceso.domain.Placa;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.Vehiculo;

public interface VehiculoRepositorio {

  Optional<Vehiculo> buscarPorPlaca(Placa placa);

  /** Lee el vehículo bloqueando su fila hasta el fin de la transacción. */
  Optional<Vehiculo> buscarPorPlacaParaActualizar(Placa placa);

  Vehiculo guardar(Vehiculo vehiculo);

  long contar();

  /** Filtro "contiene" sobre la placa normalizada; paginación y orden en la consulta. */
  Pagina<Vehiculo> listar(String filtroPlaca, ConsultaPagina consulta);

  /**
   * Vehículos de los tipos indicados con su acumulado del periodo (o ceros si no tienen fila),
   * ordenados por placa, en una sola consulta.
   */
  List<FilaInforme> acumuladosPorTipo(Set<TipoVehiculo> tipos, UUID periodoId);
}
