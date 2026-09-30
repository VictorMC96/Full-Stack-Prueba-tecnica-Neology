package mx.neology.acceso.infrastructure.persistencia;

import mx.neology.acceso.application.puerto.PeriodoRepositorio;
import mx.neology.acceso.domain.Periodo;
import org.springframework.stereotype.Repository;

@Repository
public class PeriodoRepositorioJpa implements PeriodoRepositorio {

  private final PeriodoJpaRepository repositorio;

  public PeriodoRepositorioJpa(PeriodoJpaRepository repositorio) {
    this.repositorio = repositorio;
  }

  @Override
  public Periodo activo() {
    return repositorio
        .findByActivoTrue()
        .map(PeriodoRepositorioJpa::aDominio)
        .orElseThrow(() -> new IllegalStateException("No existe un periodo activo"));
  }

  @Override
  public Periodo cerrar(Periodo periodo) {
    // saveAndFlush: uq_periodos_activo no admite dos TRUE ni dentro de la misma transacción.
    return aDominio(repositorio.saveAndFlush(aEntidad(periodo)));
  }

  @Override
  public Periodo abrir(Periodo periodo) {
    return aDominio(repositorio.save(aEntidad(periodo)));
  }

  static Periodo aDominio(PeriodoEntity e) {
    return new Periodo(
        e.getId(), e.getInicio(), e.getFin(), e.getActivo(), e.getCreatedAt(), e.getUpdatedAt());
  }

  static PeriodoEntity aEntidad(Periodo p) {
    return new PeriodoEntity(p.id(), p.inicio(), p.fin(), p.activo(), p.createdAt(), p.updatedAt());
  }
}
