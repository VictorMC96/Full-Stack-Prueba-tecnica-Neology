package com.neology.parking_backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EstanciaResponse {

    private Long id;
    private String placa;
    private LocalDateTime fechaEntrada;
    private LocalDateTime fechaSalida;
    private BigDecimal cobro;

}
