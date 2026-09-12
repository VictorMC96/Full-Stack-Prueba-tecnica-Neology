package com.estacionamiento.web;

import com.estacionamiento.error.RecursoNoEncontrado;
import com.estacionamiento.error.ReglaDeNegocio;
import com.estacionamiento.web.dto.Respuestas;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(RecursoNoEncontrado.class)
    public ResponseEntity<Respuestas.Error> noEncontrado(RecursoNoEncontrado excepcion) {
        return construir(HttpStatus.NOT_FOUND, "NO_ENCONTRADO", excepcion.getMessage());
    }

    @ExceptionHandler(ReglaDeNegocio.class)
    public ResponseEntity<Respuestas.Error> reglaDeNegocio(ReglaDeNegocio excepcion) {
        return construir(HttpStatus.CONFLICT, "REGLA_DE_NEGOCIO", excepcion.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Respuestas.Error> validacion(MethodArgumentNotValidException excepcion) {
        String detalle = excepcion.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return construir(HttpStatus.BAD_REQUEST, "SOLICITUD_INVALIDA", detalle);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Respuestas.Error> inesperado(Exception excepcion) {
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO",
                "Ocurrio un error inesperado al procesar la solicitud");
    }

    private ResponseEntity<Respuestas.Error> construir(HttpStatus estado, String codigo, String mensaje) {
        return ResponseEntity.status(estado).body(new Respuestas.Error(codigo, mensaje, LocalDateTime.now()));
    }
}
