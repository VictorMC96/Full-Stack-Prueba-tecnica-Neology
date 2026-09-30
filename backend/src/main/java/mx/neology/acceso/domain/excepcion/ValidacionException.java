package mx.neology.acceso.domain.excepcion;

/** Código {@code VALIDACION}: un campo de entrada no cumple las reglas del dominio. */
public class ValidacionException extends ExcepcionDominio {

  public static final String CODIGO = "VALIDACION";

  private final String campo;

  public ValidacionException(String campo, String mensaje) {
    super(CODIGO, mensaje);
    this.campo = campo;
  }

  public String campo() {
    return campo;
  }
}
