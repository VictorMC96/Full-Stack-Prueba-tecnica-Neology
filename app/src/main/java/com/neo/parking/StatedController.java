package com.neo.parking;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "neo/mes")
@AllArgsConstructor 
@CrossOrigin(origins = "*")
public class StatedController {

    private StayRepository stayRepository;
    private ResidentRepository residentRepository;

    @PostMapping(value = "iniciar")
    public String create() {
        stayRepository.deleteAll();

        List<Resident> residents = residentRepository.findAll();
        residents.forEach(r -> r.setCumulativeTime(0));
        residentRepository.saveAll(residents);

        return "Mes reiniciado correctamente (estancias eliminadas y tiempos en 0).";
    }
}
