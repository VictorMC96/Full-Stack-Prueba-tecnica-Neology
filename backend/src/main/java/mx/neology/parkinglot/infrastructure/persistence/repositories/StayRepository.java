package mx.neology.parkinglot.infrastructure.persistence.repositories;

import io.vavr.control.Either;
import mx.neology.parkinglot.domain.entities.Stay;
import mx.neology.parkinglot.domain.enums.ErrorTypes;

public interface StayRepository {
    Either<ErrorTypes, Stay> findBy(String license);
    Either<ErrorTypes, Stay> create(Stay stay);
    Either<ErrorTypes, Stay> update(Stay stay);
    Either<ErrorTypes, Stay> findById(Long id);
}
