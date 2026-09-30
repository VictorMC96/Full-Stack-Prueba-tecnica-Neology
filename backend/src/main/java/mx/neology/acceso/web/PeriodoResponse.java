package mx.neology.acceso.web;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PeriodoResponse(UUID id, OffsetDateTime inicio, OffsetDateTime fin) {}
