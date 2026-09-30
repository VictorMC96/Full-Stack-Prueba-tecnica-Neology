package mx.neology.acceso.infrastructure.persistencia;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoVehiculoJpaRepository extends JpaRepository<TipoVehiculoEntity, String> {

  List<TipoVehiculoEntity> findAllByOrderByCodigoAsc();

  List<TipoVehiculoEntity> findByProvisionalTrue();
}
