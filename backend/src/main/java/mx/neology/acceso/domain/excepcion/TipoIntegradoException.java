package mx.neology.acceso.domain.excepcion;

/** Código {@code TIPO_INTEGRADO}: los tipos del enunciado no se eliminan ni cambian de modelo. */
public class TipoIntegradoException extends ExcepcionDominio {

  public static final String CODIGO = "TIPO_INTEGRADO";

  public TipoIntegradoException(String codigo, String accion) {
    super(CODIGO, "El tipo de vehículo " + codigo + " está integrado en el sistema y no " + accion);
  }
}
