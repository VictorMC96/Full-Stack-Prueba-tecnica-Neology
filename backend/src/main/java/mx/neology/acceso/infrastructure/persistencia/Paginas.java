package mx.neology.acceso.infrastructure.persistencia;

import java.util.function.Function;
import mx.neology.acceso.application.modelo.ConsultaPagina;
import mx.neology.acceso.application.modelo.Pagina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/** Traduce entre la paginación de {@code application} y la de Spring Data. */
final class Paginas {

  private Paginas() {}

  static Pageable aPageable(ConsultaPagina consulta) {
    Sort orden =
        consulta.ascendente()
            ? Sort.by(consulta.campoOrden()).ascending()
            : Sort.by(consulta.campoOrden()).descending();
    return PageRequest.of(consulta.pagina(), consulta.tamano(), orden);
  }

  static <E, D> Pagina<D> aPagina(Page<E> page, Function<E, D> conversion) {
    return new Pagina<>(
        page.getContent().stream().map(conversion).toList(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages());
  }
}
