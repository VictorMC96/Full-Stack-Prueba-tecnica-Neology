package mx.neology.parkinglot.application.usecases;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import io.vavr.control.Either;
import mx.neology.parkinglot.application.services.SpotService;
import mx.neology.parkinglot.domain.entities.Spot;
import mx.neology.parkinglot.domain.enums.ErrorTypes;
import mx.neology.parkinglot.infrastructure.persistence.repositories.SpotRepository;

@Service
public class SpotUseCase implements SpotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotUseCase.class);

    private final SpotRepository spotRepository;

    public SpotUseCase(SpotRepository spotRepository) {
        this.spotRepository = spotRepository;
    }

    @Override
    public Either<ErrorTypes, Spot> create(Spot spot) {
        LOGGER.info("$ [INFO]: Executing create spot={}", spot);
        LocalDateTime now = LocalDateTime.now();
        String by = "SYSTEM";

        spot.setCreatedAt(now);
        spot.setCreatedBy(by);
        spot.setUpdatedAt(now);
        spot.setUpdatedBy(by);
        spot.setActive(true);

        return this.spotRepository.create(spot);
    }

    @Override
    public Page<Spot> findAll(int page, int size) {
        LOGGER.info("$ [INFO]: Executing findAll spots page={} size={}", page, size);
        return this.spotRepository.findAll(page, size);
    }

    @Override
    public Either<ErrorTypes, Spot> updateStatus(Long id, String status, String licensePlate) {
        var found = this.spotRepository.findById(id);
        if (found.isLeft()) {
            return Either.left(found.getLeft());
        }

        Spot spot = found.get();
        spot.setStatus(status);
        spot.setLicensePlate(licensePlate);
        spot.setUpdatedAt(LocalDateTime.now());
        spot.setUpdatedBy("SYSTEM");

        return this.spotRepository.update(spot);
    }
}