package com.neology.parking_backend.exception;

public class EstanciaYaAbiertaException extends RuntimeException {
    public EstanciaYaAbiertaException(String placa) {
        super("El vehículo con placa " + placa + " ya tiene una estancia abierta (no ha salido).");
    }
}
