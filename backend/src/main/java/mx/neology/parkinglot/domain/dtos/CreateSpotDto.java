package mx.neology.parkinglot.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.neology.parkinglot.domain.entities.Spot;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSpotDto {

    @NotBlank(message = "code is required")
    @Size(max = 10, message = "code must be at most 10 characters")
    private String code;

    @NotBlank(message = "status is required")
    @Size(max = 20, message = "status must be at most 20 characters")
    private String status;

    public Spot toEntity() {
        return Spot.builder()
                .code(code)
                .status(status)
                .build();
    }
}