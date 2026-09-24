package mx.neology.parking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.neology.parking.dto.request.PlateRequest;
import mx.neology.parking.dto.response.VehicleResponse;
import mx.neology.parking.entity.Vehicle;
import mx.neology.parking.service.VehicleService;

@RestController
@RequestMapping("/neo/vehiculos")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping("/oficiales")
    public ResponseEntity<VehicleResponse> registerOfficial(
            @Valid @RequestBody PlateRequest request
    ) {

        Vehicle vehicle =
                vehicleService.registerOfficial(request.plate());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(vehicle));
    }

    @PostMapping("/residentes")
    public ResponseEntity<VehicleResponse> registerResident(
            @Valid @RequestBody PlateRequest request
    ) {

        Vehicle vehicle =
                vehicleService.registerResident(request.plate());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(vehicle));
    }

    @PostMapping("/no-residentes")
    public ResponseEntity<VehicleResponse> registerNonResident(
            @Valid @RequestBody PlateRequest request
    ) {

        Vehicle vehicle =
                vehicleService.registerNonResident(request.plate());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(vehicle));
    }

    @GetMapping
    public List<VehicleResponse> findAll() {

        return vehicleService.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{plate}")
    public VehicleResponse findByPlate(
            @PathVariable String plate
    ) {

        return toResponse(
                vehicleService.findByPlate(plate)
        );
    }

    private VehicleResponse toResponse(Vehicle vehicle) {

        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getPlate(),
                vehicle.getType()
        );
    }
}