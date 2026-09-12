package com.estacionamiento.error;

public class SolicitudInvalida extends RuntimeException {

    public SolicitudInvalida(String mensaje) {
        super(mensaje);
    }
}
