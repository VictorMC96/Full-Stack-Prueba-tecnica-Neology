package mx.neology.parkinglot.domain.models;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import mx.neology.parkinglot.domain.enums.ErrorTypes;
import mx.neology.parkinglot.domain.enums.ResponseCodes;

public class ResponseMapper {
    
    @SuppressWarnings("static-access")
    public static <T> ResponseEntity<ResponseApp<T>> toResponse(ResponseCodes responseCodes, T data) {
        ResponseApp<T> responseApp = ResponseApp.<T>builder()
            .code(responseCodes.getHttpCode())
            .status(responseCodes.getHttpStatus())
            .message(responseCodes.getMessage())
            .timestamp(LocalDateTime.now())
            .data(data)
            .build();

        return ResponseEntity
            .status(responseCodes.getHttpCode())
            .body(responseApp);
    }

    @SuppressWarnings("static-access")
    public static <T> ResponseEntity<ResponseApp<T>> toError(ErrorTypes errorTypes) {

        ResponseApp<T> responseApp = ResponseApp.<T>builder()
            .code(errorTypes.getHttpCode())
            .status(errorTypes.getHttpStatus())
            .message(errorTypes.getErrorMessage())
            .timestamp(LocalDateTime.now())
            .data(null)
            .build();

        return ResponseEntity
            .status(errorTypes.getHttpCode())
            .body(responseApp);
    }
}
