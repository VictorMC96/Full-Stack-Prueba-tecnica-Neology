package mx.neology.parking.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mx.neology.parking.entity.Resident;
import mx.neology.parking.repository.ResidentRepository;
import mx.neology.parking.repository.StayRepository;

@Service
@RequiredArgsConstructor
public class MonthService {

    private final ResidentRepository residentRepository;
    private final StayRepository stayRepository;

    @Transactional
    public void startNewMonth() {

        List<Resident> residents = residentRepository.findAll();

        residents.forEach(
                resident -> resident.setAccumulatedMinutes(0L)
        );

        residentRepository.saveAll(residents);

        stayRepository.deleteAll();
    }
}