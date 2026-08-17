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
public class VehicleStayDetailDto {
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
    private LocalDateTime entryDate;
    @JsonProperty
    private Long stayId;
}