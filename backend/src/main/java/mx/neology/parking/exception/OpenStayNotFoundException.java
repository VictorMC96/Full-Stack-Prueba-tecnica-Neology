package mx.neology.parking.exception;

public class OpenStayNotFoundException extends RuntimeException {

    public OpenStayNotFoundException(String plate) {
        super("Vehicle with plate " + plate + " does not have an open stay");
    }
}