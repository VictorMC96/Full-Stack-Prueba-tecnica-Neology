package mx.neology.parking.exception;

public class VehicleNotFoundException extends RuntimeException {

    public VehicleNotFoundException(String plate) {
        super("Vehicle with plate " + plate + " was not found");
    }
}