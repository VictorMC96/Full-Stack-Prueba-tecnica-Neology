package mx.neology.parkinglot.infrastructure.persistence.repositories;

import org.springframework.data.domain.Page;

import io.vavr.control.Either;
import mx.neology.parkinglot.domain.entities.Spot;
import mx.neology.parkinglot.domain.enums.ErrorTypes;

public interface SpotRepository {
    Either<ErrorTypes, Spot> create(Spot spot);
    Page<Spot> findAll(int page, int size);
    Either<ErrorTypes, Spot> findById(Long id);
    Either<ErrorTypes, Spot> update(Spot spot);
}