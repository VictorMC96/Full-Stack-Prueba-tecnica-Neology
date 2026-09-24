package mx.neology.parking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.neology.parking.entity.Resident;
import mx.neology.parking.entity.Vehicle;

public interface ResidentRepository extends JpaRepository<Resident, Long> {

    Optional<Resident> findByVehicle(Vehicle vehicle);
}