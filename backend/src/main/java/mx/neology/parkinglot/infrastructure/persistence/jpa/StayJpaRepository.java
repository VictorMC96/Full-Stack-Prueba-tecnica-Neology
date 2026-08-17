package mx.neology.parkinglot.infrastructure.persistence.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.neology.parkinglot.domain.entities.Stay;

public interface StayJpaRepository extends JpaRepository<Stay, Long> {
    Optional<Stay> findByLicensePlate(String license);
}
