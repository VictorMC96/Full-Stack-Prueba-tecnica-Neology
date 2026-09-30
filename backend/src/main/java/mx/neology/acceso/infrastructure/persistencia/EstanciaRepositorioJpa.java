package mx.neology.acceso.infrastructure.persistencia;

import java.util.Optional;
import java.util.UUID;
import mx.neology.acceso.application.modelo.ConsultaPagina;
import mx.neology.acceso.application.modelo.Pagina;
import mx.neology.acceso.application.puerto.EstanciaRepositorio;
import mx.neology.acceso.domain.Estancia;
import org.springframework.stereotype.Repository;

@Repository
public class EstanciaRepositorioJpa implements EstanciaRepositorio {

  private final EstanciaJpaRepository repositorio;

  public EstanciaRepositorioJpa(EstanciaJpaRepository repositorio) {
    this.repositorio = repositorio;
  }

  @Override
  public Estancia guardar(Estancia estancia) {
    return aDominio(repositorio.save(aEntidad(estancia)));
  }

  @Override
  public Optional<Estancia> buscarPorId(UUID id) {
    return repositorio.findById(id).map(EstanciaRepositorioJpa::aDominio);
  }

  @Override
  public Pagina<Estancia> listarPorVehiculo(UUID vehiculoId, ConsultaPagina consulta) {
    return Paginas.aPagina(
        repositorio.findByVehiculoId(vehiculoId, Paginas.aPageable(consulta)),
        EstanciaRepositorioJpa::aDominio);
  }

  static Estancia aDominio(EstanciaEntity e) {
    return new Estancia(
        e.getId(),
        e.getVehiculoId(),
        e.getEntrada(),
        e.getSalida(),
        e.getMinutos(),
        e.getImporte(),
        e.getPeriodoId(),
        e.getCreatedAt(),
        e.getUpdatedAt());
  }

  static EstanciaEntity aEntidad(Estancia s) {
    return new EstanciaEntity(
        s.id(),
        s.vehiculoId(),
        s.entrada(),
        s.salida(),
        s.minutos(),
        s.importe(),
        s.periodoId(),
        s.createdAt(),
        s.updatedAt());
  }
}
