package mx.neology.acceso.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Cuerpo de las altas y de entrada/salida. */
public record PlacaRequest(
    @NotBlank(message = "La placa es obligatoria")
        @Size(min = 3, max = 20, message = "La placa debe tener entre 3 y 20 caracteres")
        String placa) {}
