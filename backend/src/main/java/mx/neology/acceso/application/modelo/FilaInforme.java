package mx.neology.acceso.application.modelo;

import java.math.BigDecimal;

/** Una fila del informe de pagos: placa, minutos e importe acumulados en el periodo. */
public record FilaInforme(String placa, long minutos, BigDecimal importe) {}
