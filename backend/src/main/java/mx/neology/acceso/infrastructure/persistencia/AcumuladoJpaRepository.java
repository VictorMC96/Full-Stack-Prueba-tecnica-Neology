package mx.neology.acceso.infrastructure.persistencia;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcumuladoJpaRepository extends JpaRepository<AcumuladoEntity, UUID> {

  Optional<AcumuladoEntity> findByVehiculoIdAndPeriodoId(UUID vehiculoId, UUID periodoId);
}
