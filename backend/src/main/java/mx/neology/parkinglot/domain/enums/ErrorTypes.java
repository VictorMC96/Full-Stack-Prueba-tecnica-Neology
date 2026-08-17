package mx.neology.parkinglot.domain.enums;

import lombok.Getter;

@Getter
public enum ErrorTypes {

    INTERNAL_SERVER_ERROR(500, "Internal server error", "Oops... Something went wrong"),
    ERROR_TO_CREATE(500, "Internal server error", "Error to insert vehicle"),
    NOT_FOUND(404, "Not found", "Resource not found"),
    NO_ACTIVE(400, "Bad Request", "Resource not available"),
    ALREADY_EXISTS(400, "Bad Request", "Resource already exists"),

    ;

    private final Integer httpCode;
    private final String httpStatus;
    private final String errorMessage;

    ErrorTypes(Integer httpCode, String httpStatus, String errorMessage) {
        this.httpCode = httpCode;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

}
