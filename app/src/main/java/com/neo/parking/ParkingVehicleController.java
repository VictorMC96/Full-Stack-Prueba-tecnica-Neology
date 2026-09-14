package com.neo.parking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "neo/vehiculos")
@AllArgsConstructor 
@Transactional 
@CrossOrigin(origins = "*")
public class ParkingVehicleController {

    private VehicleRepository repository;
    private ResidentRepository residentRepository;

    @GetMapping
    public Page<Vehicle> listVehicles(
            @RequestParam(required = false) String plate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        
        if (plate == null || plate.isBlank()) {
            return repository.findAll(pageable);
        }
        return repository.findByPlateContainingIgnoreCase(plate, pageable);
    }

    @PostMapping(value = "oficiales")
    public void create(@RequestBody VehicleRequest vehicle) {
        repository.save(vehicle.toOfficial());
    }

    @PostMapping(value = "residentes")
    public void createResident(@RequestBody VehicleRequest vehicle) {
        repository.save(vehicle.toResident());
        residentRepository.save(new Resident(null, vehicle.plate, 0));
    }

    @PostMapping(value = "no-residentes")
    public void createNotResident(@RequestBody VehicleRequest vehicle) {
        repository.save(vehicle.toNoResident());
    }

}
