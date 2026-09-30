package mx.neology.acceso.infrastructure.persistencia;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstanciaJpaRepository extends JpaRepository<EstanciaEntity, UUID> {

  Page<EstanciaEntity> findByVehiculoId(UUID vehiculoId, Pageable pageable);
}
