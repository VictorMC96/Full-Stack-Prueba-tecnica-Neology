package mx.neology.acceso.infrastructure.persistencia;

import java.util.List;
import java.util.Optional;
import mx.neology.acceso.application.puerto.TipoVehiculoRepositorio;
import mx.neology.acceso.domain.CodigoTipo;
import mx.neology.acceso.domain.TipoVehiculo;
import org.springframework.stereotype.Repository;

@Repository
public class TipoVehiculoRepositorioJpa implements TipoVehiculoRepositorio {

  private final TipoVehiculoJpaRepository repositorio;

  public TipoVehiculoRepositorioJpa(TipoVehiculoJpaRepository repositorio) {
    this.repositorio = repositorio;
  }

  @Override
  public List<TipoVehiculo> listar() {
    return repositorio.findAllByOrderByCodigoAsc().stream()
        .map(TipoVehiculoRepositorioJpa::aDominio)
        .toList();
  }

  @Override
  public Optional<TipoVehiculo> buscar(CodigoTipo codigo) {
    return repositorio.findById(codigo.valor()).map(TipoVehiculoRepositorioJpa::aDominio);
  }

  @Override
  public TipoVehiculo provisional() {
    List<TipoVehiculoEntity> provisionales = repositorio.findByProvisionalTrue();
    if (provisionales.size() != 1) {
      throw new IllegalStateException(
          "Debe existir exactamente un tipo de vehículo provisional; hay " + provisionales.size());
    }
    return aDominio(provisionales.get(0));
  }

  @Override
  public TipoVehiculo guardar(TipoVehiculo tipo) {
    return aDominio(repositorio.save(aEntidad(tipo)));
  }

  @Override
  public void eliminar(CodigoTipo codigo) {
    repositorio.deleteById(codigo.valor());
  }

  static TipoVehiculo aDominio(TipoVehiculoEntity e) {
    return new TipoVehiculo(
        new CodigoTipo(e.getCodigo()),
        e.getNombre(),
        e.getModeloCobro(),
        e.getTarifaPorMinuto(),
        e.isProvisional(),
        e.isIntegrado(),
        e.getCreatedAt(),
        e.getUpdatedAt());
  }

  static TipoVehiculoEntity aEntidad(TipoVehiculo t) {
    return new TipoVehiculoEntity(
        t.codigo().valor(),
        t.nombre(),
        t.modeloCobro(),
        t.tarifaPorMinuto(),
        t.provisional(),
        t.integrado(),
        t.createdAt(),
        t.updatedAt());
  }
}
