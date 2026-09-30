package mx.neology.acceso.infrastructure.persistencia;

import java.util.Optional;
import java.util.UUID;
import mx.neology.acceso.application.puerto.AcumuladoRepositorio;
import mx.neology.acceso.domain.Acumulado;
import org.springframework.stereotype.Repository;

@Repository
public class AcumuladoRepositorioJpa implements AcumuladoRepositorio {

  private final AcumuladoJpaRepository repositorio;

  public AcumuladoRepositorioJpa(AcumuladoJpaRepository repositorio) {
    this.repositorio = repositorio;
  }

  @Override
  public Optional<Acumulado> buscar(UUID vehiculoId, UUID periodoId) {
    return repositorio
        .findByVehiculoIdAndPeriodoId(vehiculoId, periodoId)
        .map(AcumuladoRepositorioJpa::aDominio);
  }

  @Override
  public Acumulado guardar(Acumulado acumulado) {
    return aDominio(repositorio.save(aEntidad(acumulado)));
  }

  static Acumulado aDominio(AcumuladoEntity e) {
    return new Acumulado(
        e.getId(),
        e.getVehiculoId(),
        e.getPeriodoId(),
        e.getMinutos(),
        e.getImporte(),
        e.getCreatedAt(),
        e.getUpdatedAt());
  }

  static AcumuladoEntity aEntidad(Acumulado a) {
    return new AcumuladoEntity(
        a.id(),
        a.vehiculoId(),
        a.periodoId(),
        a.minutos(),
        a.importe(),
        a.createdAt(),
        a.updatedAt());
  }
}
