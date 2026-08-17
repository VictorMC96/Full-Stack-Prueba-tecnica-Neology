package mx.neology.parkinglot.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.neology.parkinglot.domain.entities.Vehicle;

public interface VehicleJpaRepository extends JpaRepository<Vehicle, String> {

}
