package api.estacionamiento.repository;

import api.estacionamiento.models.Residente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IResidenteRepository extends JpaRepository<Residente, String> {

}

