package mx.neology.acceso.infrastructure.persistencia;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodoJpaRepository extends JpaRepository<PeriodoEntity, UUID> {

  Optional<PeriodoEntity> findByActivoTrue();
}
