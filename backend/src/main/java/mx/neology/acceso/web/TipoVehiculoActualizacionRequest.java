package mx.neology.acceso.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import mx.neology.acceso.domain.ModeloCobro;

/** Cuerpo de actualización de un tipo de vehículo (el código va en la ruta). */
public record TipoVehiculoActualizacionRequest(
    @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 40, message = "El nombre debe tener entre 2 y 40 caracteres")
        String nombre,
    @NotNull(message = "El modelo de cobro es obligatorio") ModeloCobro modeloCobro,
    @NotNull(message = "La tarifa por minuto es obligatoria")
        @DecimalMin(
            value = "0.00",
            message = "La tarifa por minuto debe ser mayor o igual que 0.00")
        @Digits(
            integer = 10,
            fraction = 2,
            message = "La tarifa por minuto admite como máximo 2 decimales")
        BigDecimal tarifaPorMinuto) {}
