package mx.neology.acceso.domain.excepcion;

/** Código {@code TIPO_NO_ENCONTRADO}. */
public class TipoNoEncontradoException extends ExcepcionDominio {

  public static final String CODIGO = "TIPO_NO_ENCONTRADO";

  public TipoNoEncontradoException(String codigo) {
    super(CODIGO, "No existe un tipo de vehículo con código " + codigo);
  }
}
