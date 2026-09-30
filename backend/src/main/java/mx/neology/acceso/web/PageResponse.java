package mx.neology.acceso.web;

import java.util.List;
import java.util.function.Function;
import mx.neology.acceso.application.modelo.Pagina;

public record PageResponse<T>(
    List<T> content, int page, int size, long totalElements, int totalPages) {

  public static <D, T> PageResponse<T> de(Pagina<D> pagina, Function<D, T> conversion) {
    return new PageResponse<>(
        pagina.contenido().stream().map(conversion).toList(),
        pagina.pagina(),
        pagina.tamano(),
        pagina.totalElementos(),
        pagina.totalPaginas());
  }
}
