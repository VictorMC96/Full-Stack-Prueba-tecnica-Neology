package com.neo.parking;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResidentRepository extends JpaRepository<Resident, UUID> {
    Optional<Resident> findByPlate(String plate);
    Page<Resident> findByPlateContainingIgnoreCase(String plate, Pageable pageable);
}