package mx.neology.parkinglot.application.usecases;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.vavr.control.Either;
import mx.neology.parkinglot.application.services.StayService;
import mx.neology.parkinglot.application.services.VehicleService;
import mx.neology.parkinglot.domain.dtos.CreateEntryStayDto;
import mx.neology.parkinglot.domain.dtos.UpdateStayDto;
import mx.neology.parkinglot.domain.entities.Stay;
import mx.neology.parkinglot.domain.enums.ErrorTypes;
import mx.neology.parkinglot.infrastructure.persistence.repositories.StayRepository;

@Service
public class StayUseCase implements StayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleUseCase.class);
    private final StayRepository stayRepository;
    private final VehicleService vehicleService;

    public StayUseCase(StayRepository stayRepository, VehicleService vehicleService) {
        this.stayRepository = stayRepository;
        this.vehicleService = vehicleService;
    }

    @Override
    public Either<ErrorTypes, Stay> findOne(String lincese) {
        LOGGER.info("$ [INFO]: Executing findOne(license={})", lincese);
        var found = this.stayRepository.findBy(lincese);

        if (found.isLeft()) {
            LOGGER.error("$ [ERROR]: Stay not found");
            return Either.left(found.getLeft());
        }

        if (found.isRight() && found.get().getIsActive().equals(Boolean.FALSE)) {
            LOGGER.error("$ [ERROR]: Stay is not active");
            return Either.left(ErrorTypes.NO_ACTIVE);
        }

        return found;

    }
    

    @Override
    public Either<ErrorTypes, Stay> create(CreateEntryStayDto createStayDto) {
        LOGGER.info("$ [INFO]: Executing create new stay");
        LocalDateTime now = LocalDateTime.now();
        String by = "SYSTEM";

        var vehicle = this.vehicleService.findOne(createStayDto.getLicensePlate());
        if (vehicle.isLeft()) {
            LOGGER.error("$ [ERROR]: Vehicle not found");
            return Either.left(vehicle.getLeft());
        }
        LOGGER.info("$ [INFO]: Vehicle found={}", vehicle.get());

        Stay newStay = Stay.builder()
            .licensePlate(vehicle.get().getLicensePlate())
            .entryDate(createStayDto.getEntryDate())
            .exitDate(null)
            .vehicle(vehicle.get())
            .createdAt(now)
            .createdBy(by)
            .updatedAt(now)
            .updatedBy(by)
            .isActive(true)
            .build();
        LOGGER.info("$ [INFO]: Create stay={}", newStay);

        return this.stayRepository.create(newStay);
    }

    @Override
    public Either<ErrorTypes, Stay> update(String lincese, UpdateStayDto updateStayDto) {
        
        Either<ErrorTypes, Stay> found = this.findOne(lincese);

        found.get().setExitDate(updateStayDto.getExitDate());
        found.get().setUpdatedAt(LocalDateTime.now());
        found.get().setUpdatedBy("SYSTEM");

        return this.stayRepository.update(found.get());
    }

    @Override
    public Either<ErrorTypes, Stay> findById(Long id) {
        LOGGER.info("$ [INFO]: Executing findById(id={})", id);
        return this.stayRepository.findById(id);
    }
}
