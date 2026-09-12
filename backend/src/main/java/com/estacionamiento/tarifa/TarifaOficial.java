package com.estacionamiento.tarifa;

import com.estacionamiento.dominio.TipoVehiculo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TarifaOficial implements Tarifa {

    @Override
    public TipoVehiculo tipo() {
        return TipoVehiculo.OFICIAL;
    }

    @Override
    public BigDecimal calcular(long minutos) {
        return new BigDecimal("0.00");
    }

    @Override
    public boolean seCobraAlSalir() {
        return true;
    }
}
