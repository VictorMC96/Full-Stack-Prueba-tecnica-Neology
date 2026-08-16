package mx.neology.parkinglot.domain.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailPaymentDto {
    private Long id;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String status;
    private Long stayId;
}