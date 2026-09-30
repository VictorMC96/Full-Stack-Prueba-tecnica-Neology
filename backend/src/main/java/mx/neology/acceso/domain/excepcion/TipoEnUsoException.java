package mx.neology.acceso.domain.excepcion;

/** Código {@code TIPO_EN_USO}: un tipo con vehículos registrados no puede eliminarse. */
public class TipoEnUsoException extends ExcepcionDominio {

  public static final String CODIGO = "TIPO_EN_USO";

  public TipoEnUsoException(String codigo, long vehiculos) {
    super(
        CODIGO,
        "El tipo de vehículo "
            + codigo
            + " tiene "
            + vehiculos
            + " vehículo(s) y no puede eliminarse");
  }
}
