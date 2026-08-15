package com.neology.parking_backend.service.tarifa;

import java.math.BigDecimal;

import com.neology.parking_backend.model.TipoVehiculo;

public interface CalcularTarifa {
    TipoVehiculo getTipo();
    BigDecimal calcular(long minutos);
}
