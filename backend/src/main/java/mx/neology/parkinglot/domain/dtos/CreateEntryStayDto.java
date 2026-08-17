package mx.neology.parkinglot.domain.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateEntryStayDto {

    @NotNull(message = "License plate cannot be null")
    @NotBlank(message = "License plate is mandatory")
    @Size(min = 6, max = 7, message = "License must be between 6 and 7 characters")
    @JsonProperty
    private String licensePlate;

    @FutureOrPresent(message = "The date cannot be earlier than today")    
    @JsonProperty
    private LocalDateTime entryDate;

}
