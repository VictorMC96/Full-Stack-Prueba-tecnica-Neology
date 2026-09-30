package mx.neology.acceso.domain.excepcion;

/** Código {@code VALIDACION} sobre el campo {@code placa}. */
public class PlacaInvalidaException extends ValidacionException {

  public PlacaInvalidaException(String placa) {
    super("placa", "La placa '" + placa + "' no es válida: use de 3 a 10 letras o dígitos");
  }
}
