package mx.neology.acceso.application.modelo;

import java.math.BigDecimal;
import java.util.List;
import mx.neology.acceso.domain.Periodo;

/** Informe de pagos del periodo activo. */
public record InformePagos(
    Periodo periodo, List<FilaInforme> filas, long totalMinutos, BigDecimal totalImporte) {}
