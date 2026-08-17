package mx.neology.parkinglot.domain.models;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseApp<T> {
    
    private Integer code;
    private String status;
    private String message;
    private LocalDateTime timestamp;
    private T data;
}
