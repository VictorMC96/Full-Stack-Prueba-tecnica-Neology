package mx.neology.parkinglot.domain.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
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
public class CreatePaymentDto {

    @NotNull(message = "stayId is required")
    private Long stayId;

    @NotNull(message = "amount is required")
    private BigDecimal amount;

    private String status;
}