package mx.neology.parking.exception;

public class VehicleAlreadyExistsException extends RuntimeException {

    public VehicleAlreadyExistsException(String plate) {
        super("Vehicle with plate " + plate + " already exists");
    }
}