package mx.neology.parking.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mx.neology.parking.entity.Resident;
import mx.neology.parking.entity.Stay;
import mx.neology.parking.entity.Vehicle;
import mx.neology.parking.enums.VehicleType;
import mx.neology.parking.exception.OpenStayNotFoundException;
import mx.neology.parking.exception.StayAlreadyOpenException;
import mx.neology.parking.repository.ResidentRepository;
import mx.neology.parking.repository.StayRepository;

@Service
@RequiredArgsConstructor
public class StayService {

    private final StayRepository stayRepository;
    private final ResidentRepository residentRepository;
    private final VehicleService vehicleService;
    private final PaymentService paymentService;

    @Transactional
    public Stay registerEntry(String plate) {

        Vehicle vehicle =
                vehicleService.findOrCreateNonResident(plate);

        if (stayRepository.existsByVehicleAndExitDateTimeIsNull(vehicle)) {
            throw new StayAlreadyOpenException(vehicle.getPlate());
        }

        Stay stay = Stay.builder()
                .vehicle(vehicle)
                .entryDateTime(LocalDateTime.now())
                .build();

        return stayRepository.save(stay);
    }

    @Transactional
    public Stay registerExit(String plate) {

        Vehicle vehicle = vehicleService.findByPlate(plate);

        Stay stay = stayRepository
                .findFirstByVehicleAndExitDateTimeIsNullOrderByEntryDateTimeDesc(vehicle)
                .orElseThrow(() ->
                        new OpenStayNotFoundException(vehicle.getPlate())
                );

        LocalDateTime exitDateTime = LocalDateTime.now();

        long minutes = Duration.between(
                stay.getEntryDateTime(),
                exitDateTime
        ).toMinutes();

        BigDecimal amount =
                paymentService.calculateAmount(
                        vehicle.getType(),
                        minutes
                );

        stay.setExitDateTime(exitDateTime);
        stay.setDurationMinutes(minutes);
        stay.setAmount(amount);

        if (vehicle.getType() == VehicleType.RESIDENT) {
            accumulateResidentMinutes(vehicle, minutes);
        }

        return stayRepository.save(stay);
    }

    @Transactional(readOnly = true)
    public List<Stay> findByVehicle(String plate) {

        Vehicle vehicle = vehicleService.findByPlate(plate);

        return stayRepository
                .findByVehicleOrderByEntryDateTimeDesc(vehicle);
    }

    private void accumulateResidentMinutes(
            Vehicle vehicle,
            long minutes
    ) {

        Resident resident = residentRepository
                .findByVehicle(vehicle)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Resident data not found for vehicle "
                                        + vehicle.getPlate()
                        )
                );

        resident.setAccumulatedMinutes(
                resident.getAccumulatedMinutes() + minutes
        );

        residentRepository.save(resident);
    }
}