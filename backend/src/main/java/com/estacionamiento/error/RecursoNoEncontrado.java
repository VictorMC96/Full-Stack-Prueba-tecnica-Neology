package com.estacionamiento.error;

public class RecursoNoEncontrado extends RuntimeException {

    public RecursoNoEncontrado(String mensaje) {
        super(mensaje);
    }
}
