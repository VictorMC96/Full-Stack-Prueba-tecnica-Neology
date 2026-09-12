package com.estacionamiento.error;

//Error-SolicitudInvalida
public class SolicitudInvalida extends RuntimeException {

    public SolicitudInvalida(String mensaje) {
        super(mensaje);
    }
}
