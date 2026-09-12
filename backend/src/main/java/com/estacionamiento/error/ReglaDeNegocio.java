package com.estacionamiento.error;

public class ReglaDeNegocio extends RuntimeException {

    public ReglaDeNegocio(String mensaje) {
        super(mensaje);
    }
}
