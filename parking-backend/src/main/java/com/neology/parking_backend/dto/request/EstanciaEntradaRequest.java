package com.neology.parking_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EstanciaEntradaRequest {

    @NotBlank(message = "La placa es obligatoria")
    private String placa;

}
