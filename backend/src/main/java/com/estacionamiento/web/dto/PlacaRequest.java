package com.estacionamiento.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PlacaRequest(
        @NotBlank(message = "La placa es obligatoria")
        @Pattern(regexp = "^[A-Za-z0-9 -]+$", message = "La placa solo admite letras, numeros, espacios y guion")
        String placa) {
}
