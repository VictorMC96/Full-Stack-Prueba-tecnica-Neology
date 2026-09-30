package mx.neology.acceso.application.modelo;

import mx.neology.acceso.domain.Periodo;

/** Periodo cerrado y periodo recién abierto al iniciar un mes. */
public record ResultadoIniciarMes(Periodo periodoAnterior, Periodo periodoActual) {}
