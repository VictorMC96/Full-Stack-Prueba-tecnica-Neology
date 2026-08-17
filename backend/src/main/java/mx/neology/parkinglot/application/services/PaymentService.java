package mx.neology.parkinglot.application.services;

import org.springframework.data.domain.Page;

import io.vavr.control.Either;
import mx.neology.parkinglot.domain.dtos.CreatePaymentDto;
import mx.neology.parkinglot.domain.entities.Payment;
import mx.neology.parkinglot.domain.enums.ErrorTypes;

public interface PaymentService {
    Either<ErrorTypes, Payment> create(CreatePaymentDto createPaymentDto);
    Page<Payment> findAll(int page, int size);
}