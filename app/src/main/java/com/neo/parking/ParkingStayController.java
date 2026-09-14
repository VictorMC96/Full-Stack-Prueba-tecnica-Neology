package com.neo.parking;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "neo/estancias")
@AllArgsConstructor
@Transactional
@CrossOrigin(origins = "*")
public class ParkingStayController {

    private VehicleRepository vehicleRepository;
    private StayRepository stayRepository;
    private ResidentRepository residentRepository;

    @GetMapping
    public Page<Stay> listStays(
            @RequestParam(required = false) String plate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("entryDate").descending());
        
        if (plate == null || plate.isBlank()) {
            return stayRepository.findAll(pageable);
        }
        return stayRepository.findByPlateContainingIgnoreCase(plate, pageable);
    }

    @PostMapping(value = "entrada")
    public void create(@RequestBody VehicleRequest vehicleRequest) {
        Optional<Vehicle> vehicleOptional = this.vehicleRepository.findByPlate(vehicleRequest.plate);
        Vehicle vehicle = vehicleOptional.orElseThrow(() -> new RuntimeException("Vehículo no registrado"));
        stayRepository.save(new Stay(null, vehicle.getPlate(), LocalDateTime.now(), null));
    }

    @PostMapping(value = "salida")
    public ExitResponse createExit(@RequestBody VehicleRequest vehicleRequest) {
        Stay stay = stayRepository.findByPlateAndExitDateIsNull(
                vehicleRequest.plate, Sort.by("entryDate").ascending()
        ).orElseThrow(() -> new RuntimeException("Estancia no encontrada"));
        
        stay.setExitDate(LocalDateTime.now());
        stayRepository.save(stay);

        long minutes = Duration.between(stay.getEntryDate(), stay.getExitDate()).toMinutes();
        Vehicle vehicle = vehicleRepository.findByPlate(vehicleRequest.plate)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        double payment = 0.0;

        switch (vehicle.getType()) {
            case NO_RESIDENT:
                payment = minutes * 0.5;
                break;
            case RESIDENT:
                Resident resident = residentRepository.findByPlate(vehicle.getPlate())
                        .orElseThrow(() -> new RuntimeException("Residente no encontrado"));
                resident.setCumulativeTime(resident.getCumulativeTime() + (int)minutes);
                residentRepository.save(resident);
                break;
            case OFFICIALS:
                break;
        }

        return new ExitResponse(vehicle.getPlate(), payment);
    }
}