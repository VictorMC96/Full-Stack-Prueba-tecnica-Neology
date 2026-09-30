package mx.neology.acceso.web;

import java.util.Set;
import mx.neology.acceso.application.modelo.ConsultaPagina;
import mx.neology.acceso.domain.excepcion.ParametroInvalidoException;

/** Valida page/size/sort y los convierte en una {@link ConsultaPagina}. */
final class ParametrosPagina {

  static final int TAMANO_MAXIMO = 100;

  private ParametrosPagina() {}

  static ConsultaPagina de(int page, int size, String sort, Set<String> camposPermitidos) {
    if (page < 0) {
      throw new ParametroInvalidoException("page debe ser mayor o igual que 0");
    }
    if (size < 1) {
      throw new ParametroInvalidoException("size debe ser mayor o igual que 1");
    }
    String[] partes = sort == null ? new String[0] : sort.split(",");
    String campo = partes.length > 0 && !partes[0].isBlank() ? partes[0].trim() : "placa";
    String direccion = partes.length > 1 ? partes[1].trim().toLowerCase() : "asc";
    if (!camposPermitidos.contains(campo)) {
      throw new ParametroInvalidoException(
          "sort solo admite los campos " + String.join(", ", camposPermitidos));
    }
    if (!direccion.equals("asc") && !direccion.equals("desc")) {
      throw new ParametroInvalidoException("sort solo admite las direcciones asc y desc");
    }
    return new ConsultaPagina(page, Math.min(size, TAMANO_MAXIMO), campo, direccion.equals("asc"));
  }
}
