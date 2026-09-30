package mx.neology.acceso.web;

import java.math.BigDecimal;
import java.util.List;

public record InformePagosResponse(
    PeriodoResponse periodo, List<Fila> filas, long totalMinutos, BigDecimal totalImporte) {

  public record Fila(String placa, long minutos, BigDecimal importe) {}
}
