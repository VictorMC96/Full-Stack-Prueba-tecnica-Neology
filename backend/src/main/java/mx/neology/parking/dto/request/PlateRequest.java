package mx.neology.parking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlateRequest(

        @NotBlank(message = "Plate is required")
        @Size(max = 20, message = "Plate must not exceed 20 characters")
        String plate

) {
}