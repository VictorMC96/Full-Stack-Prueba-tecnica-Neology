package mx.neology.parkinglot.infrastructure.persistence.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.vavr.control.Either;
import mx.neology.parkinglot.domain.entities.Payment;
import mx.neology.parkinglot.domain.enums.ErrorTypes;

public interface PaymentRepository {
    Either<ErrorTypes, Payment> create(Payment payment);
    Page<Payment> findAll(Pageable pageable);
}