package com.estacionamiento.tarifa;

import com.estacionamiento.dominio.TipoVehiculo;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class ResolutorTarifas {

    private final Map<TipoVehiculo, Tarifa> tarifas = new EnumMap<>(TipoVehiculo.class);

    public ResolutorTarifas(List<Tarifa> registradas) {
        registradas.forEach(tarifa -> tarifas.put(tarifa.tipo(), tarifa));
    }

    public Tarifa para(TipoVehiculo tipo) {
        Tarifa tarifa = tarifas.get(tipo);
        if (tarifa == null) {
            throw new IllegalStateException("No hay tarifa registrada para el tipo " + tipo);
        }
        return tarifa;
    }
}
