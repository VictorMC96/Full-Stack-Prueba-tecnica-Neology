package mx.neology.acceso.domain.excepcion;

/** Código {@code TIPO_YA_REGISTRADO}. */
public class TipoYaRegistradoException extends ExcepcionDominio {

  public static final String CODIGO = "TIPO_YA_REGISTRADO";

  public TipoYaRegistradoException(String codigo) {
    super(CODIGO, "Ya existe un tipo de vehículo con código " + codigo);
  }
}
