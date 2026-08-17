package mx.neology.parkinglot.infrastructure.persistence.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.vavr.control.Either;
import mx.neology.parkinglot.domain.entities.Spot;
import mx.neology.parkinglot.domain.enums.ErrorTypes;
import mx.neology.parkinglot.infrastructure.persistence.jpa.SpotJpaRepository;
import mx.neology.parkinglot.infrastructure.persistence.repositories.SpotRepository;

@Repository
public class SpotDao implements SpotRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotDao.class);

    private final SpotJpaRepository spotJpaRepository;

    public SpotDao(SpotJpaRepository spotJpaRepository) {
        this.spotJpaRepository = spotJpaRepository;
    }

    @Transactional
    @Override
    public Either<ErrorTypes, Spot> create(Spot spot) {
        try {
            return Either.right(this.spotJpaRepository.saveAndFlush(spot));
        } catch (Exception e) {
            LOGGER.error("$ [ERROR]: Error to insert spot={}", e.getMessage(), e);
            return Either.left(ErrorTypes.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Page<Spot> findAll(int page, int size) {
        LOGGER.info("$ [INFO]: Executing findAll jpa method...");
        Pageable pageable = PageRequest.of(page, size, Sort.by("code").ascending());
        return this.spotJpaRepository.findAll(pageable);
    }

    @Override
    public Either<ErrorTypes, Spot> findById(Long id) {
        try {
            return this.spotJpaRepository.findById(id)
                .<Either<ErrorTypes, Spot>>map(Either::right)
                .orElse(Either.left(ErrorTypes.NOT_FOUND));
        } catch (Exception e) {
            LOGGER.error("$ [ERROR]: Error finding spot by id={}", e.getMessage(), e);
            return Either.left(ErrorTypes.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @Override
    public Either<ErrorTypes, Spot> update(Spot spot) {
        try {
            return Either.right(this.spotJpaRepository.saveAndFlush(spot));
        } catch (Exception e) {
            LOGGER.error("$ [ERROR]: Error updating spot={}", e.getMessage(), e);
            return Either.left(ErrorTypes.INTERNAL_SERVER_ERROR);
        }
    }
}