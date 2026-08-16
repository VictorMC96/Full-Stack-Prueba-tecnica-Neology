package mx.neology.parkinglot.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.neology.parkinglot.domain.entities.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}