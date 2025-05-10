package api.estacionamiento.repository;

import api.estacionamiento.models.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVehiculoRepository  extends JpaRepository<Vehiculo, String> {}