package com.neology.parking_backend.exception;

public class VehiculoYaExisteException extends RuntimeException {
    public VehiculoYaExisteException(String placa) {
        super("Ya existe un vehículo registrado con la placa: " + placa);
    }
}
