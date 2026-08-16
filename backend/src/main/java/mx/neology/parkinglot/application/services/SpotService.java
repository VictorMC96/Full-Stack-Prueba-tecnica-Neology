package mx.neology.parkinglot.application.services;

import org.springframework.data.domain.Page;

import io.vavr.control.Either;
import mx.neology.parkinglot.domain.entities.Spot;
import mx.neology.parkinglot.domain.enums.ErrorTypes;

public interface SpotService {
    Either<ErrorTypes, Spot> create(Spot spot);
    Page<Spot> findAll(int page, int size);
    Either<ErrorTypes, Spot> updateStatus(Long id, String status, String licensePlate);
}