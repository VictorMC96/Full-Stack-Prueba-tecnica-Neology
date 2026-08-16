package mx.neology.parkinglot.infrastructure.persistence.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.vavr.control.Either;
import jakarta.persistence.NoResultException;
import mx.neology.parkinglot.domain.entities.Vehicle;
import mx.neology.parkinglot.domain.enums.ErrorTypes;
import mx.neology.parkinglot.infrastructure.persistence.jpa.VehicleJpaRepository;
import mx.neology.parkinglot.infrastructure.persistence.repositories.VehicleRepository;

@Repository
public class VehicleDao implements VehicleRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleDao.class);
    private final VehicleJpaRepository vehicleJpaRepository;

    public VehicleDao(VehicleJpaRepository vehicleJpaRepository) {
        this.vehicleJpaRepository = vehicleJpaRepository;
    }

    @Override
    public Either<ErrorTypes, Vehicle> findOne(String license) {
        try {
            return this.vehicleJpaRepository.findById(license)
                .<Either<ErrorTypes, Vehicle>>map(found -> Either.right(found))
                .orElse(Either.left(ErrorTypes.NOT_FOUND));
        } catch (NoResultException e) {
            LOGGER.error("[ERROR]: Not found by license, NoResultException: {}", e.getMessage());
            return Either.left(ErrorTypes.NOT_FOUND);
        } catch (Exception e) {
            LOGGER.error("[ERROR]: Not found by license: {}", e.getMessage());
            return Either.left(ErrorTypes.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @Override
    public Either<ErrorTypes, Vehicle> create(Vehicle vehicle) {

        try {
            return Either.right(this.vehicleJpaRepository.saveAndFlush(vehicle));
        } catch (Exception e) {
            LOGGER.error("$ [ERROR]: Error to insert vechicle={}", e.getMessage(), e);
            return Either.left(ErrorTypes.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public Page<Vehicle> findAll(Pageable pageable) {
        LOGGER.info("$ [INFO]: Executing findAll jpa method...");
        return this.vehicleJpaRepository.findAll(pageable);
    }
}
