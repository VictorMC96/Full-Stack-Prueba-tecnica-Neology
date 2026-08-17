package mx.neology.parkinglot.domain.models;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorApp {
    private String message;
    private String status;
    private Integer code;
    private LocalDateTime timestamp;
}
