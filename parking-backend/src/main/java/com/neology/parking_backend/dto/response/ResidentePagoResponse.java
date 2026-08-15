package com.neology.parking_backend.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ResidentePagoResponse {

    private String placa;
    private Long tiempoAcumuladoMinutos;
    private BigDecimal pagoAcumulado;

}
