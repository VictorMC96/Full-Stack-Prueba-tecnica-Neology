package mx.neology.acceso.web;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import mx.neology.acceso.domain.excepcion.ExcepcionDominio;
import mx.neology.acceso.domain.excepcion.ParametroInvalidoException;
import mx.neology.acceso.domain.excepcion.PlacaInvalidaException;
import mx.neology.acceso.domain.excepcion.VehiculoNoDentroException;
import mx.neology.acceso.domain.excepcion.VehiculoNoEncontradoException;
import mx.neology.acceso.domain.excepcion.VehiculoYaDentroException;
import mx.neology.acceso.domain.excepcion.VehiculoYaRegistradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** Todo error sale como {@link ProblemDetail} con la propiedad {@code code}. */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

  static final String CODE = "code";
  static final String ERRORS = "errors";

  private static final Map<String, HttpStatus> ESTADOS =
      Map.of(
          PlacaInvalidaException.CODIGO, HttpStatus.BAD_REQUEST,
          ParametroInvalidoException.CODIGO, HttpStatus.BAD_REQUEST,
          VehiculoNoEncontradoException.CODIGO, HttpStatus.NOT_FOUND,
          VehiculoYaRegistradoException.CODIGO, HttpStatus.CONFLICT,
          VehiculoYaDentroException.CODIGO, HttpStatus.CONFLICT,
          VehiculoNoDentroException.CODIGO, HttpStatus.CONFLICT);

  @ExceptionHandler(ExcepcionDominio.class)
  public ProblemDetail dominio(ExcepcionDominio ex, HttpServletRequest peticion) {
    HttpStatus estado = ESTADOS.getOrDefault(ex.codigo(), HttpStatus.BAD_REQUEST);
    ProblemDetail problema = ProblemDetail.forStatusAndDetail(estado, ex.getMessage());
    problema.setProperty(CODE, ex.codigo());
    if (ex instanceof PlacaInvalidaException) {
      problema.setProperty(ERRORS, List.of(error("placa", ex.getMessage())));
    }
    log.warn(
        "evento=error_negocio code={} status={} ruta={} detalle={}",
        ex.codigo(),
        estado.value(),
        peticion.getRequestURI(),
        ex.getMessage());
    return problema;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail interno(Exception ex, HttpServletRequest peticion) {
    log.error("evento=error_interno ruta={} tipo={}", peticion.getRequestURI(), ex.getClass(), ex);
    ProblemDetail problema =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno; inténtelo de nuevo");
    problema.setProperty(CODE, "ERROR_INTERNO");
    return problema;
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    ProblemDetail problema =
        ProblemDetail.forStatusAndDetail(status, "La petición contiene campos inválidos");
    problema.setProperty(CODE, PlacaInvalidaException.CODIGO);
    problema.setProperty(
        ERRORS,
        ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> error(fe.getField(), fe.getDefaultMessage()))
            .toList());
    log.warn("evento=error_validacion campos={}", ex.getBindingResult().getFieldErrorCount());
    return handleExceptionInternal(ex, problema, headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    ProblemDetail problema =
        ProblemDetail.forStatusAndDetail(status, "El cuerpo de la petición no es válido");
    problema.setProperty(CODE, PlacaInvalidaException.CODIGO);
    problema.setProperty(ERRORS, List.of());
    log.warn("evento=error_cuerpo detalle={}", ex.getMessage());
    return handleExceptionInternal(ex, problema, headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> createResponseEntity(
      Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
    if (body instanceof ProblemDetail problema
        && (problema.getProperties() == null || !problema.getProperties().containsKey(CODE))) {
      problema.setProperty(CODE, codigoPorEstado(statusCode));
    }
    return super.createResponseEntity(body, headers, statusCode, request);
  }

  private static String codigoPorEstado(HttpStatusCode estado) {
    if (estado.value() == HttpStatus.BAD_REQUEST.value()) {
      return ParametroInvalidoException.CODIGO;
    }
    if (estado.is5xxServerError()) {
      return "ERROR_INTERNO";
    }
    HttpStatus resuelto = HttpStatus.resolve(estado.value());
    return resuelto == null ? "ERROR_HTTP" : resuelto.name();
  }

  private static Map<String, String> error(String campo, String mensaje) {
    return Map.of("field", campo, "message", mensaje == null ? "valor inválido" : mensaje);
  }
}
