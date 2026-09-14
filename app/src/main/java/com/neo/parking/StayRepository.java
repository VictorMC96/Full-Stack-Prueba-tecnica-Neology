package com.neo.parking;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StayRepository extends JpaRepository<Stay, UUID>{
    Optional<Stay> findByPlateAndExitDateIsNull(String plate,Sort sort);
    Page<Stay> findByPlateContainingIgnoreCase(String plate, Pageable pageable);
}
