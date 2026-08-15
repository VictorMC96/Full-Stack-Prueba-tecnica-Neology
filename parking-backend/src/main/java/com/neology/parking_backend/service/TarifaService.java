package com.neology.parking_backend.service;

import java.math.BigDecimal;

import com.neology.parking_backend.model.TipoVehiculo;

public interface TarifaService {

    BigDecimal calcularCobro(TipoVehiculo tipo, long minutos);

}
