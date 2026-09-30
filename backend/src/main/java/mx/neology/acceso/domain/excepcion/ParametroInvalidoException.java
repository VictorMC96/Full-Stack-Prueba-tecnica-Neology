package mx.neology.acceso.domain.excepcion;

/** Código {@code PARAMETRO_INVALIDO}: un parámetro de consulta fuera de rango o desconocido. */
public class ParametroInvalidoException extends ExcepcionDominio {

  public static final String CODIGO = "PARAMETRO_INVALIDO";

  public ParametroInvalidoException(String mensaje) {
    super(CODIGO, mensaje);
  }
}
