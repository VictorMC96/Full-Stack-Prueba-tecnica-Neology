package mx.neology.acceso.domain.excepcion;

/** Código {@code VEHICULO_YA_REGISTRADO}. */
public class VehiculoYaRegistradoException extends ExcepcionDominio {

  public static final String CODIGO = "VEHICULO_YA_REGISTRADO";

  public VehiculoYaRegistradoException(String placa) {
    super(CODIGO, "El vehículo con placa " + placa + " ya está registrado");
  }
}
