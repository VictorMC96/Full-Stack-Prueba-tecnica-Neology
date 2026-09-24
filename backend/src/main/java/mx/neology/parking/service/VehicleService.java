package mx.neology.parking.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mx.neology.parking.entity.Resident;
import mx.neology.parking.entity.Vehicle;
import mx.neology.parking.enums.VehicleType;
import mx.neology.parking.exception.VehicleAlreadyExistsException;
import mx.neology.parking.exception.VehicleNotFoundException;
import mx.neology.parking.repository.ResidentRepository;
import mx.neology.parking.repository.VehicleRepository;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ResidentRepository residentRepository;

    @Transactional
    public Vehicle registerOfficial(String plate) {
        return registerVehicle(plate, VehicleType.OFFICIAL);
    }

    @Transactional
    public Vehicle registerResident(String plate) {
        String normalizedPlate = normalizePlate(plate);

        validateVehicleDoesNotExist(normalizedPlate);

        Vehicle vehicle = Vehicle.builder()
                .plate(normalizedPlate)
                .type(VehicleType.RESIDENT)
                .build();

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        Resident resident = Resident.builder()
                .vehicle(savedVehicle)
                .accumulatedMinutes(0L)
                .build();

        residentRepository.save(resident);

        return savedVehicle;
    }

    @Transactional
    public Vehicle registerNonResident(String plate) {
        return registerVehicle(plate, VehicleType.NON_RESIDENT);
    }

    @Transactional(readOnly = true)
    public Vehicle findByPlate(String plate) {
        String normalizedPlate = normalizePlate(plate);

        return vehicleRepository.findByPlate(normalizedPlate)
                .orElseThrow(() ->
                        new VehicleNotFoundException(normalizedPlate)
                );
    }

    @Transactional
    public Vehicle findOrCreateNonResident(String plate) {
        String normalizedPlate = normalizePlate(plate);

        return vehicleRepository.findByPlate(normalizedPlate)
                .orElseGet(() -> {
                    Vehicle vehicle = Vehicle.builder()
                            .plate(normalizedPlate)
                            .type(VehicleType.NON_RESIDENT)
                            .build();

                    return vehicleRepository.save(vehicle);
                });
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    private Vehicle registerVehicle(String plate, VehicleType type) {
        String normalizedPlate = normalizePlate(plate);

        validateVehicleDoesNotExist(normalizedPlate);

        Vehicle vehicle = Vehicle.builder()
                .plate(normalizedPlate)
                .type(type)
                .build();

        return vehicleRepository.save(vehicle);
    }

    private void validateVehicleDoesNotExist(String plate) {
        if (vehicleRepository.existsByPlate(plate)) {
            throw new VehicleAlreadyExistsException(plate);
        }
    }

    private String normalizePlate(String plate) {
        return plate.trim().toUpperCase();
    }
}