package mx.neology.acceso.infrastructure.persistencia;

import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import mx.neology.acceso.domain.TipoVehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VehiculoJpaRepository extends JpaRepository<VehiculoEntity, UUID> {

  Optional<VehiculoEntity> findByPlaca(String placa);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select v from VehiculoEntity v where v.placa = :placa")
  Optional<VehiculoEntity> buscarParaActualizar(@Param("placa") String placa);

  Page<VehiculoEntity> findByPlacaContaining(String filtro, Pageable pageable);

  @Query(
      "select v.placa, a.minutos, a.importe from VehiculoEntity v "
          + "left join AcumuladoEntity a on a.vehiculoId = v.id and a.periodoId = :periodoId "
          + "where v.tipo in :tipos order by v.placa asc")
  List<Object[]> acumuladosPorTipo(
      @Param("tipos") Collection<TipoVehiculo> tipos, @Param("periodoId") UUID periodoId);
}
