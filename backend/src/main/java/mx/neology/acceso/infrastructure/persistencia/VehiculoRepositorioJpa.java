package mx.neology.acceso.infrastructure.persistencia;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import mx.neology.acceso.application.modelo.ConsultaPagina;
import mx.neology.acceso.application.modelo.FilaInforme;
import mx.neology.acceso.application.modelo.Pagina;
import mx.neology.acceso.application.puerto.VehiculoRepositorio;
import mx.neology.acceso.domain.Placa;
import mx.neology.acceso.domain.TipoVehiculo;
import mx.neology.acceso.domain.Vehiculo;
import org.springframework.stereotype.Repository;

@Repository
public class VehiculoRepositorioJpa implements VehiculoRepositorio {

  private final VehiculoJpaRepository repositorio;

  public VehiculoRepositorioJpa(VehiculoJpaRepository repositorio) {
    this.repositorio = repositorio;
  }

  @Override
  public Optional<Vehiculo> buscarPorPlaca(Placa placa) {
    return repositorio.findByPlaca(placa.valor()).map(VehiculoRepositorioJpa::aDominio);
  }

  @Override
  public Optional<Vehiculo> buscarPorPlacaParaActualizar(Placa placa) {
    return repositorio.buscarParaActualizar(placa.valor()).map(VehiculoRepositorioJpa::aDominio);
  }

  @Override
  public Vehiculo guardar(Vehiculo vehiculo) {
    return aDominio(repositorio.save(aEntidad(vehiculo)));
  }

  @Override
  public long contar() {
    return repositorio.count();
  }

  @Override
  public Pagina<Vehiculo> listar(String filtroPlaca, ConsultaPagina consulta) {
    return Paginas.aPagina(
        repositorio.findByPlacaContaining(filtroPlaca, Paginas.aPageable(consulta)),
        VehiculoRepositorioJpa::aDominio);
  }

  @Override
  public List<FilaInforme> acumuladosPorTipo(Set<TipoVehiculo> tipos, UUID periodoId) {
    if (tipos.isEmpty()) {
      return List.of();
    }
    return repositorio.acumuladosPorTipo(tipos, periodoId).stream()
        .map(
            fila ->
                new FilaInforme(
                    (String) fila[0],
                    fila[1] == null ? 0L : ((Number) fila[1]).longValue(),
                    fila[2] == null ? BigDecimal.ZERO : (BigDecimal) fila[2]))
        .toList();
  }

  static Vehiculo aDominio(VehiculoEntity e) {
    return new Vehiculo(
        e.getId(),
        new Placa(e.getPlaca()),
        e.getTipo(),
        e.getEstanciaAbiertaId(),
        e.getCreatedAt(),
        e.getUpdatedAt());
  }

  static VehiculoEntity aEntidad(Vehiculo v) {
    return new VehiculoEntity(
        v.id(), v.placa().valor(), v.tipo(), v.estanciaAbiertaId(), v.createdAt(), v.updatedAt());
  }
}
