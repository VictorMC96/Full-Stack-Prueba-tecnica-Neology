package mx.neology.parkinglot.domain.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class DetailVehicleDto {

    @JsonProperty
    private String licensePlate;
    @JsonProperty
    private String type;
    @JsonProperty
    private String brand;
    @JsonProperty
    private String model;
    @JsonProperty
    private String color;
    @JsonProperty
    private String bodyType;
    @JsonProperty
    private Integer year;
    @JsonProperty
    private String countryPlate;
    @JsonProperty
    private LocalDateTime createdAt;
    @JsonProperty
    private String createdBy;
    @JsonProperty
    private LocalDateTime updatedAt;
    @JsonProperty
    private String updatedBy;
    @JsonProperty
    private boolean isActive;
}