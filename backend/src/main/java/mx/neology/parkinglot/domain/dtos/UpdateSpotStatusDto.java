package mx.neology.parkinglot.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSpotStatusDto {

    @NotBlank(message = "status is required")
    private String status;

    private String licensePlate;
}