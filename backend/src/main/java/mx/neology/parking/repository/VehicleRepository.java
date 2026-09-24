package mx.neology.parking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.neology.parking.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByPlate(String plate);

    boolean existsByPlate(String plate);
}