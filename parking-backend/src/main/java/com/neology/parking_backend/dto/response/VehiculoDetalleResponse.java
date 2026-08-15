package com.neology.parking_backend.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.neology.parking_backend.model.TipoVehiculo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VehiculoDetalleResponse {

    private String placa;
    private TipoVehiculo tipo;
    private List<EstanciaResponse> estancias;
    private Long tiempoAcumuladoMinutos;
    private BigDecimal pagoAcumulado;

}
