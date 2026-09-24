package mx.neology.parking.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mx.neology.parking.dto.response.ResidentPaymentResponse;
import mx.neology.parking.entity.Resident;
import mx.neology.parking.enums.VehicleType;
import mx.neology.parking.repository.ResidentRepository;

@Service
@RequiredArgsConstructor
public class ResidentService {

    private final ResidentRepository residentRepository;
    private final PaymentService paymentService;

    @Transactional(readOnly = true)
    public List<ResidentPaymentResponse> generatePaymentReport() {

        return residentRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(
                        resident -> resident.getVehicle().getPlate()
                ))
                .map(this::toPaymentResponse)
                .toList();
    }

    private ResidentPaymentResponse toPaymentResponse(
            Resident resident
    ) {

        BigDecimal amount = paymentService.calculateAmount(
                VehicleType.RESIDENT,
                resident.getAccumulatedMinutes()
        );

        return new ResidentPaymentResponse(
                resident.getVehicle().getPlate(),
                resident.getAccumulatedMinutes(),
                amount
        );
    }
}