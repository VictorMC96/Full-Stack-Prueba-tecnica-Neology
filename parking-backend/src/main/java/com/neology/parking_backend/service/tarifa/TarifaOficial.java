package com.neology.parking_backend.service.tarifa;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import com.neology.parking_backend.model.TipoVehiculo;

@Component
public class TarifaOficial implements CalcularTarifa{

    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.OFICIAL;
    }

    @Override
    public BigDecimal calcular(long minutos) {
        return BigDecimal.ZERO;
    }

}
