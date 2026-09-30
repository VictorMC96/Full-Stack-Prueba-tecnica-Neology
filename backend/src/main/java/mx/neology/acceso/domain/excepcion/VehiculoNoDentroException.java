package mx.neology.acceso.domain.excepcion;

/** Código {@code VEHICULO_NO_DENTRO}. */
public class VehiculoNoDentroException extends ExcepcionDominio {

  public static final String CODIGO = "VEHICULO_NO_DENTRO";

  public VehiculoNoDentroException(String placa) {
    super(CODIGO, "El vehículo con placa " + placa + " no tiene una entrada registrada");
  }
}
