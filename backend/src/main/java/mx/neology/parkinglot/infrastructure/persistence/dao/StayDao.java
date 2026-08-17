package mx.neology.parkinglot.infrastructure.persistence.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import io.vavr.control.Either;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import mx.neology.parkinglot.domain.entities.Stay;
import mx.neology.parkinglot.domain.enums.ErrorTypes;
import mx.neology.parkinglot.infrastructure.persistence.jpa.StayJpaRepository;
import mx.neology.parkinglot.infrastructure.persistence.repositories.StayRepository;

@Repository
public class StayDao implements StayRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleDao.class);
    private final StayJpaRepository stayJpaRepository;

    public StayDao(StayJpaRepository stayJpaRepository) {
        this.stayJpaRepository = stayJpaRepository;
    }

    @Override
    public Either<ErrorTypes, Stay> findBy(String license) {
        try {
            return this.stayJpaRepository.findByLicensePlate(license)
                .<Either<ErrorTypes, Stay>>map(found -> Either.right(found))
                .orElse(Either.left(ErrorTypes.NOT_FOUND));
        } catch (NoResultException e) {
            LOGGER.error("[ERROR]: Not found by criteria, NoResultException: {}", e.getMessage());
            return Either.left(ErrorTypes.NOT_FOUND);
        } catch (Exception e) {
            LOGGER.error("[ERROR]: Not found by criteria: {}", e.getMessage());
            return Either.left(ErrorTypes.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @Override
    public Either<ErrorTypes, Stay> create(Stay stay) {
        
        try {
            return Either.right(this.stayJpaRepository.saveAndFlush(stay));
        } catch (Exception e) {
            LOGGER.error("$ [ERROR]: Error to insert vechicle={}", e.getMessage(), e);
            return Either.left(ErrorTypes.INTERNAL_SERVER_ERROR);
        }
        
    }

    @Override
    public Either<ErrorTypes, Stay> update(Stay stay) {
        return this.create(stay);
    }

    @Override
    public Either<ErrorTypes, Stay> findById(Long id) {
        try {
            return this.stayJpaRepository.findById(id)
                .<Either<ErrorTypes, Stay>>map(Either::right)
                .orElse(Either.left(ErrorTypes.NOT_FOUND));
        } catch (Exception e) {
            LOGGER.error("$ [ERROR]: Error finding stay by id={}", e.getMessage(), e);
            return Either.left(ErrorTypes.INTERNAL_SERVER_ERROR);
        }
    }
    
}
