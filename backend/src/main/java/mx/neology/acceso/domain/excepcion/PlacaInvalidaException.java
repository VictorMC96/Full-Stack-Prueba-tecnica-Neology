package mx.neology.acceso.domain.excepcion;

/** Código {@code VALIDACION}. */
public class PlacaInvalidaException extends ExcepcionDominio {

  public static final String CODIGO = "VALIDACION";

  public PlacaInvalidaException(String placa) {
    super(CODIGO, "La placa '" + placa + "' no es válida: use de 3 a 10 letras o dígitos");
  }
}
