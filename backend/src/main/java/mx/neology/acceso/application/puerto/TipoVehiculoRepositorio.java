package mx.neology.acceso.application.puerto;

import java.util.List;
import java.util.Optional;
import mx.neology.acceso.domain.CodigoTipo;
import mx.neology.acceso.domain.TipoVehiculo;

public interface TipoVehiculoRepositorio {

  /** Todos los tipos ordenados por código. */
  List<TipoVehiculo> listar();

  Optional<TipoVehiculo> buscar(CodigoTipo codigo);

  /** El único tipo provisional; falla si no hay exactamente uno. */
  TipoVehiculo provisional();

  TipoVehiculo guardar(TipoVehiculo tipo);

  void eliminar(CodigoTipo codigo);
}
