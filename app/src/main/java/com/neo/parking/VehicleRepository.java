package com.neo.parking;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    Optional<Vehicle> findByPlate(String plate);
    Page<Vehicle> findByPlateContainingIgnoreCase(String plate, Pageable pageable);
}
