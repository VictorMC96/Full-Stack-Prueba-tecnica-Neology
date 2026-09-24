package mx.neology.parking.exception;

public class StayAlreadyOpenException extends RuntimeException {

    public StayAlreadyOpenException(String plate) {
        super("Vehicle with plate " + plate + " already has an open stay");
    }
}