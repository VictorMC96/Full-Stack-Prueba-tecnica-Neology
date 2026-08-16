package mx.neology.parkinglot.application.services;


import org.springframework.data.domain.Page;

import io.vavr.control.Either;
import mx.neology.parkinglot.domain.dtos.CreateVehicleDto;
import mx.neology.parkinglot.domain.entities.Vehicle;
import mx.neology.parkinglot.domain.enums.ErrorTypes;

public interface VehicleService {
    
    Either<ErrorTypes, Vehicle> findOne(String lincese);
    Either<ErrorTypes, Vehicle> create(CreateVehicleDto createVehicleDto);
    Page<Vehicle> findAll(int page, int size);
}
