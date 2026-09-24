package mx.neology.parking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.neology.parking.entity.Stay;
import mx.neology.parking.entity.Vehicle;

public interface StayRepository extends JpaRepository<Stay, Long> {

    Optional<Stay> findFirstByVehicleAndExitDateTimeIsNullOrderByEntryDateTimeDesc(
            Vehicle vehicle
    );

    boolean existsByVehicleAndExitDateTimeIsNull(Vehicle vehicle);

    List<Stay> findByVehicleOrderByEntryDateTimeDesc(Vehicle vehicle);
}