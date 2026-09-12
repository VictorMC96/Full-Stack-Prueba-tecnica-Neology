package com.estacionamiento.web;

import com.estacionamiento.error.RecursoNoEncontrado;
import com.estacionamiento.error.ReglaDeNegocio;
import com.estacionamiento.error.SolicitudInvalida;
import com.estacionamiento.web.dto.Respuestas;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

//Manejo-Errores
@RestControllerAdvice
public class ManejadorErrores extends ResponseEntityExceptionHandler {

    //Error-404
    @ExceptionHandler(RecursoNoEncontrado.class)
    public ResponseEntity<Respuestas.Error> noEncontrado(RecursoNoEncontrado excepcion) {
        return construir(HttpStatus.NOT_FOUND, "NO_ENCONTRADO", excepcion.getMessage());
    }

    //Error-409
    @ExceptionHandler(ReglaDeNegocio.class)
    public ResponseEntity<Respuestas.Error> reglaDeNegocio(ReglaDeNegocio excepcion) {
        return construir(HttpStatus.CONFLICT, "REGLA_DE_NEGOCIO", excepcion.getMessage());
    }

    //Error-400
    @ExceptionHandler(SolicitudInvalida.class)
    public ResponseEntity<Respuestas.Error> solicitudInvalida(SolicitudInvalida excepcion) {
        return construir(HttpStatus.BAD_REQUEST, "SOLICITUD_INVALIDA", excepcion.getMessage());
    }

    //Error-500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Respuestas.Error> inesperado(Exception excepcion) {
        logger.error("Error inesperado al procesar la solicitud", excepcion);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO",
                "Ocurrio un error inesperado al procesar la solicitud");
    }

    //Errores-Validacion
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException excepcion, HttpHeaders cabeceras,
            HttpStatusCode estado, WebRequest peticion) {

        String detalle = excepcion.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.badRequest()
                .body(new Respuestas.Error("SOLICITUD_INVALIDA", detalle, LocalDateTime.now()));
    }

    //Errores-Spring
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception excepcion, Object cuerpo, HttpHeaders cabeceras,
            HttpStatusCode estado, WebRequest peticion) {

        return ResponseEntity.status(estado).headers(cabeceras)
                .body(new Respuestas.Error(codigoDe(estado), mensajeDe(excepcion, estado), LocalDateTime.now()));
    }

    //Mensaje-Traducido
    private String mensajeDe(Exception excepcion, HttpStatusCode estado) {
        if (estado.isSameCodeAs(HttpStatus.NOT_FOUND)) {
            return "La ruta solicitada no existe";
        }
        if (estado.isSameCodeAs(HttpStatus.METHOD_NOT_ALLOWED)) {
            return "El metodo HTTP no esta permitido para esta ruta";
        }
        return excepcion.getMessage();
    }

    //Codigo-PorEstado
    private String codigoDe(HttpStatusCode estado) {
        if (estado.isSameCodeAs(HttpStatus.NOT_FOUND)) {
            return "NO_ENCONTRADO";
        }
        if (estado.isSameCodeAs(HttpStatus.METHOD_NOT_ALLOWED)) {
            return "METODO_NO_PERMITIDO";
        }
        if (estado.is4xxClientError()) {
            return "SOLICITUD_INVALIDA";
        }
        return "ERROR_INTERNO";
    }

    //Armado-Respuesta
    private ResponseEntity<Respuestas.Error> construir(HttpStatus estado, String codigo, String mensaje) {
        return ResponseEntity.status(estado).body(new Respuestas.Error(codigo, mensaje, LocalDateTime.now()));
    }
}
