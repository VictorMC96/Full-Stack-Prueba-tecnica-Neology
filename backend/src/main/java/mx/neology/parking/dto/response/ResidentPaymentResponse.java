package mx.neology.parking.dto.response;

import java.math.BigDecimal;

public record ResidentPaymentResponse(
        String plate,
        long accumulatedMinutes,
        BigDecimal amount
) {
}