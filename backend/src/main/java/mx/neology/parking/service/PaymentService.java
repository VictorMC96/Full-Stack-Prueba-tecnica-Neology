package mx.neology.parking.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import mx.neology.parking.enums.VehicleType;

@Service
public class PaymentService {

    private static final BigDecimal RESIDENT_RATE = new BigDecimal("0.05");
    private static final BigDecimal NON_RESIDENT_RATE = new BigDecimal("0.50");

    public BigDecimal calculateAmount(VehicleType vehicleType, long minutes) {

        if (minutes < 0) {
            throw new IllegalArgumentException("Minutes cannot be negative");
        }

        return switch (vehicleType) {
            case OFFICIAL -> BigDecimal.ZERO;
            case RESIDENT -> RESIDENT_RATE.multiply(BigDecimal.valueOf(minutes));
            case NON_RESIDENT -> NON_RESIDENT_RATE.multiply(BigDecimal.valueOf(minutes));
        };
    }
}