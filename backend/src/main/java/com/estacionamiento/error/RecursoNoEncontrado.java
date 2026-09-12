package com.estacionamiento.error;

//Error-NoEncontrado
public class RecursoNoEncontrado extends RuntimeException {

    public RecursoNoEncontrado(String mensaje) {
        super(mensaje);
    }
}
