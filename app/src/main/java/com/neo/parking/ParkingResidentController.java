package com.neo.parking;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "neo/residentes")
@AllArgsConstructor 
@CrossOrigin(origins = "*")
public class ParkingResidentController {
    private ResidentRepository residentRepository;

    @GetMapping(value = "pagos")
    public List<ResidentPaymentResponse> index() {
        return residentRepository.findAll().stream()
                .map(r -> new ResidentPaymentResponse(
                        r.getPlate(),
                        r.getCumulativeTime(),
                        r.getCumulativeTime() * 0.05))
                .collect(Collectors.toList());
    }
}
