package com.neology.parking_backend.service.tarifa;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import com.neology.parking_backend.model.TipoVehiculo;

@Component
public class TarifaNoResidente implements CalcularTarifa{

    private static final BigDecimal TARIFA_POR_MINUTO = BigDecimal.valueOf(0.5);

    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.NO_RESIDENTE;
    }

    @Override
    public BigDecimal calcular(long minutos) {
        return TARIFA_POR_MINUTO.multiply(BigDecimal.valueOf(minutos))
                .setScale(2, RoundingMode.HALF_UP);
    }

}
