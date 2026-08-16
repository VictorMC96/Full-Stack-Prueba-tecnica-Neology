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
public class DetailStayDto {

    @JsonProperty
    private String licensePlate;

    @JsonProperty
    private LocalDateTime entryDate;

    @JsonProperty
    private LocalDateTime exitDate;

}
