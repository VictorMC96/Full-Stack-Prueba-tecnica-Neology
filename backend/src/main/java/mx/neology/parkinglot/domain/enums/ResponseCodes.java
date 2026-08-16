package mx.neology.parkinglot.domain.enums;

import lombok.Getter;

@Getter
public enum ResponseCodes {

    CREATED(201, "Created", "Resource created successfully"),
    UPDATED(202, "Accepted", "Resource updated successfully"),
    GET_ALL(200, "OK", "All resources were successfully obtained"),

    ;

    private final Integer httpCode;
    private final String httpStatus;
    private final String message;

    ResponseCodes(Integer httpCode, String httpStatus, String message) {
        this.httpCode = httpCode;
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
