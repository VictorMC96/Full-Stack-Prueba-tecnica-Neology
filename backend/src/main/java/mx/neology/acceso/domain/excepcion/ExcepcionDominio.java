package mx.neology.acceso.domain.excepcion;

/** Error de negocio con un código estable que la capa web traduce a un estado HTTP. */
public abstract class ExcepcionDominio extends RuntimeException {

  private final String codigo;

  protected ExcepcionDominio(String codigo, String mensaje) {
    super(mensaje);
    this.codigo = codigo;
  }

  public String codigo() {
    return codigo;
  }
}
