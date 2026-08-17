package mx.neology.parkinglot.infrastructure.persistence.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.neology.parkinglot.domain.entities.Spot;

public interface SpotJpaRepository extends JpaRepository<Spot, Long> {
    Optional<Spot> findByCode(String code);
    boolean existsByCode(String code);
}