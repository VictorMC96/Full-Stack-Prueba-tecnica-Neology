package mx.neology.parkinglot.infrastructure.persistence.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.vavr.control.Either;
import mx.neology.parkinglot.domain.entities.Vehicle;
import mx.neology.parkinglot.domain.enums.ErrorTypes;

public interface VehicleRepository {

    Either<ErrorTypes, Vehicle> findOne(String licenst);
    Either<ErrorTypes, Vehicle> create(Vehicle vehicle);
    Page<Vehicle> findAll(Pageable pageable);

}
