package mx.neology.parkinglot.application.services;

import io.vavr.control.Either;
import mx.neology.parkinglot.domain.dtos.CreateEntryStayDto;
import mx.neology.parkinglot.domain.dtos.UpdateStayDto;
import mx.neology.parkinglot.domain.entities.Stay;
import mx.neology.parkinglot.domain.enums.ErrorTypes;

public interface StayService {
    
    Either<ErrorTypes, Stay> findOne(String lincese);
    Either<ErrorTypes, Stay> create(CreateEntryStayDto stay);
    Either<ErrorTypes, Stay> update(String lincese, UpdateStayDto stay);
    Either<ErrorTypes, Stay> findById(Long id);
}
