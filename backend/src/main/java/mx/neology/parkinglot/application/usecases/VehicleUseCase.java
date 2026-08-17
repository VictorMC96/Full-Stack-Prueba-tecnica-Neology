package mx.neology.parkinglot.application.usecases;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.vavr.control.Either;
import mx.neology.parkinglot.application.services.StayService;
import mx.neology.parkinglot.application.services.VehicleService;
import mx.neology.parkinglot.domain.dtos.CreateEntryStayDto;
import mx.neology.parkinglot.domain.dtos.CreateVehicleDto;
import mx.neology.parkinglot.domain.entities.Vehicle;
import mx.neology.parkinglot.domain.enums.ErrorTypes;
import mx.neology.parkinglot.infrastructure.persistence.repositories.VehicleRepository;

@Service
public class VehicleUseCase implements VehicleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleUseCase.class);

    private final VehicleRepository vehicleRepository;
    private final StayService stayService;

    public VehicleUseCase(VehicleRepository vehicleRepository, @Lazy StayService stayService) {
        this.vehicleRepository = vehicleRepository;
        this.stayService = stayService;
    }

    @Override
    public Either<ErrorTypes, Vehicle> findOne(String license) {
        var found = this.vehicleRepository.findOne(license);

        if (found.isLeft()) {
            LOGGER.error("$ [ERROR]: Vehicle not found");
            return Either.left(found.getLeft());
        }

        if (found.isRight() && !found.get().isActive()) {
            LOGGER.error("$ [ERROR]: Vehicle is not active");
            return Either.left(ErrorTypes.NO_ACTIVE);
        }

        return found;
    }

    @Transactional
    @Override
    public Either<ErrorTypes, Vehicle> create(CreateVehicleDto createVehicleDto) {

        var found = this.vehicleRepository.findOne(createVehicleDto.getLicensePlate());
        if (found.isRight()) {
            LOGGER.error("$ [ERROR]: This vehicle already exists");
            return Either.left(ErrorTypes.ALREADY_EXISTS);
        }

        LocalDateTime now = LocalDateTime.now();
        String by = "SYSTEM";

        Vehicle vehicle = Vehicle.builder()
                .licensePlate(createVehicleDto.getLicensePlate())
                .type(createVehicleDto.getType())
                .brand(createVehicleDto.getBrand())
                .model(createVehicleDto.getModel())
                .color(createVehicleDto.getColor())
                .bodyType(createVehicleDto.getBodyType())
                .year(createVehicleDto.getYear())
                .countryPlate(createVehicleDto.getCountryPlate())
                .createdAt(now)
                .createdBy(by)
                .updatedAt(now)
                .updatedBy(by)
                .isActive(true)
                .build();

        var createdVehicle = this.vehicleRepository.create(vehicle);
        if (createdVehicle.isLeft()) {
            throw new IllegalStateException("Vehicle creation failed: " + createdVehicle.getLeft());
        }

        CreateEntryStayDto entryDto = CreateEntryStayDto.builder()
                .licensePlate(createVehicleDto.getLicensePlate())
                .entryDate(now)
                .build();

        var createdStay = this.stayService.create(entryDto);
        if (createdStay.isLeft()) {
            throw new IllegalStateException("Stay creation failed: " + createdStay.getLeft());
        }

        return createdVehicle;
    }

    @Override
    public Page<Vehicle> findAll(int page, int size) {
        LOGGER.info("$ [INFO]: Executing findAll method");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return this.vehicleRepository.findAll(pageable);
    }
}