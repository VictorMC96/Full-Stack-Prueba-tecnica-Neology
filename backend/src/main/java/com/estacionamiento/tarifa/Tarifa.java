package com.estacionamiento.tarifa;

import com.estacionamiento.dominio.TipoVehiculo;

import java.math.BigDecimal;

//Contrato-Tarifa
public interface Tarifa {

    TipoVehiculo tipo();

    BigDecimal calcular(long minutos);

    boolean seCobraAlSalir();
}
