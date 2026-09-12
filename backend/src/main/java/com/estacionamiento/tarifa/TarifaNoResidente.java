package com.estacionamiento.tarifa;

import com.estacionamiento.dominio.TipoVehiculo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class TarifaNoResidente implements Tarifa {

    private static final BigDecimal POR_MINUTO = new BigDecimal("0.50");

    @Override
    public TipoVehiculo tipo() {
        return TipoVehiculo.NO_RESIDENTE;
    }

    @Override
    public BigDecimal calcular(long minutos) {
        return POR_MINUTO.multiply(BigDecimal.valueOf(minutos)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean seCobraAlSalir() {
        return true;
    }
}
