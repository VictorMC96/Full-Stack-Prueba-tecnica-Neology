package com.neology.parking_backend.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.neology.parking_backend.model.TipoVehiculo;
import com.neology.parking_backend.service.TarifaService;
import com.neology.parking_backend.service.tarifa.CalcularTarifa;

@Service
public class TarifaServiceImpl implements TarifaService {

    private final Map<TipoVehiculo, CalcularTarifa> calculadorasPorTipo;

    public TarifaServiceImpl(List<CalcularTarifa> calculadoras) {
        this.calculadorasPorTipo = calculadoras.stream()
                .collect(Collectors.toMap(CalcularTarifa::getTipo, Function.identity()));
    }

    @Override
    public BigDecimal calcularCobro(TipoVehiculo tipo, long minutos) {
        CalcularTarifa calculadora = calculadorasPorTipo.get(tipo);
        if (calculadora == null) {
            throw new IllegalStateException("No hay una calculadora de tarifa registrada para el tipo: " + tipo);
        }
        return calculadora.calcular(minutos);
    }

}
