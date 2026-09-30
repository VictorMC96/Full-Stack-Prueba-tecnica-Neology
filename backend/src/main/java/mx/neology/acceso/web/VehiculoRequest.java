package mx.neology.acceso.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Alta genérica: placa y código de un tipo del catálogo. */
public record VehiculoRequest(
    @NotBlank(message = "La placa es obligatoria")
        @Size(min = 3, max = 20, message = "La placa debe tener entre 3 y 20 caracteres")
        String placa,
    @NotBlank(message = "El tipo es obligatorio")
        @Size(min = 3, max = 20, message = "El tipo debe tener entre 3 y 20 caracteres")
        String tipo) {}
