package com.estacionamiento.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

//Entrada-AltaVehiculo
public record AltaVehiculoRequest(
        @NotBlank(message = "La placa es obligatoria")
        @Pattern(regexp = "^[A-Za-z0-9 -]+$", message = "La placa solo admite letras, numeros, espacios y guion")
        String placa,

        @Size(max = 120, message = "El propietario no puede exceder 120 caracteres")
        String propietario) {
}
