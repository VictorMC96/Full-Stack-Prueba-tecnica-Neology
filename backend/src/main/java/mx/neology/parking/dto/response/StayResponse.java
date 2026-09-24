package mx.neology.parking.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StayResponse(
        Long id,
        String plate,
        LocalDateTime entryDateTime,
        LocalDateTime exitDateTime,
        Long durationMinutes,
        BigDecimal amount
) {
}