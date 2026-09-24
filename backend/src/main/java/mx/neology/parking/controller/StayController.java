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
import mx.neology.parking.dto.response.StayResponse;
import mx.neology.parking.entity.Stay;
import mx.neology.parking.service.StayService;

@RestController
@RequestMapping("/neo/estancias")
@RequiredArgsConstructor
public class StayController {

    private final StayService stayService;

    @PostMapping("/entrada")
    public ResponseEntity<StayResponse> registerEntry(
            @Valid @RequestBody PlateRequest request
    ) {

        Stay stay = stayService.registerEntry(request.plate());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(stay));
    }

    @PostMapping("/salida")
    public StayResponse registerExit(
            @Valid @RequestBody PlateRequest request
    ) {

        return toResponse(
                stayService.registerExit(request.plate())
        );
    }

    @GetMapping("/vehiculo/{plate}")
    public List<StayResponse> findByVehicle(
            @PathVariable String plate
    ) {

        return stayService.findByVehicle(plate)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private StayResponse toResponse(Stay stay) {

        return new StayResponse(
                stay.getId(),
                stay.getVehicle().getPlate(),
                stay.getEntryDateTime(),
                stay.getExitDateTime(),
                stay.getDurationMinutes(),
                stay.getAmount()
        );
    }
}