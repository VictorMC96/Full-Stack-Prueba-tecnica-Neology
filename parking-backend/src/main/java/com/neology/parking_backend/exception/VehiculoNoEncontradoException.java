package com.neology.parking_backend.exception;

public class VehiculoNoEncontradoException extends RuntimeException {
    public VehiculoNoEncontradoException(String placa) {
        super("No existe un vehículo registrado con la placa: " + placa);
    }
}
