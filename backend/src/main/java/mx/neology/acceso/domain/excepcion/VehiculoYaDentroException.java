package mx.neology.acceso.domain.excepcion;

/** Código {@code VEHICULO_YA_DENTRO}. */
public class VehiculoYaDentroException extends ExcepcionDominio {

  public static final String CODIGO = "VEHICULO_YA_DENTRO";

  public VehiculoYaDentroException(String placa) {
    super(CODIGO, "El vehículo con placa " + placa + " ya se encuentra dentro");
  }
}
