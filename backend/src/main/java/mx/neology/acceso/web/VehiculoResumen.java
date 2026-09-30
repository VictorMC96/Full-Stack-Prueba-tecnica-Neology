package mx.neology.acceso.web;

import java.time.OffsetDateTime;

public record VehiculoResumen(
    String placa, String tipo, String tipoNombre, boolean dentro, OffsetDateTime createdAt) {}
