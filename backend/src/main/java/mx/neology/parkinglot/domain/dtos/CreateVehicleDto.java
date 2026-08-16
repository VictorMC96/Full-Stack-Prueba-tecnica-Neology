package mx.neology.parkinglot.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class CreateVehicleDto {

    @NotBlank(message = "licensePlate is required")
    @Size(max = 10, message = "licensePlate must be at most 10 characters")
    private String licensePlate;

    // El type lo fija el controller (OFFICIAL / RESIDENT / NO_RESIDENT),
    // por eso no lleva @NotBlank.
    private String type;

    @Size(max = 50, message = "brand must be at most 50 characters")
    private String brand;

    @Size(max = 50, message = "model must be at most 50 characters")
    private String model;

    @Size(max = 30, message = "color must be at most 30 characters")
    private String color;

    @Size(max = 30, message = "bodyType must be at most 30 characters")
    private String bodyType;

    private Integer year;

    @Size(max = 40, message = "countryPlate must be at most 40 characters")
    private String countryPlate;
}