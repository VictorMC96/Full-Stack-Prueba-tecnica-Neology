package com.estacionamiento.error;

//Error-ReglaNegocio
public class ReglaDeNegocio extends RuntimeException {

    public ReglaDeNegocio(String mensaje) {
        super(mensaje);
    }
}
