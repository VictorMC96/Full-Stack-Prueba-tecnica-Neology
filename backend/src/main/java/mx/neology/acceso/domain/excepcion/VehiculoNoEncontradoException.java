package mx.neology.acceso.domain.excepcion;

/** Código {@code VEHICULO_NO_ENCONTRADO}. */
public class VehiculoNoEncontradoException extends ExcepcionDominio {

  public static final String CODIGO = "VEHICULO_NO_ENCONTRADO";

  public VehiculoNoEncontradoException(String placa) {
    super(CODIGO, "No existe un vehículo con placa " + placa);
  }
}
