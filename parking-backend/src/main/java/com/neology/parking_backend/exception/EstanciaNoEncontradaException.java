package com.neology.parking_backend.exception;

public class EstanciaNoEncontradaException extends RuntimeException {
    public EstanciaNoEncontradaException(String placa) {
        super("No se encontró una estancia abierta para la placa: " + placa);
    }
}