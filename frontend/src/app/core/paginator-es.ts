import { MatPaginatorIntl } from '@angular/material/paginator';

/** Etiquetas del paginador de Angular Material en español. */
export function paginadorEnEspanol(): MatPaginatorIntl {
  const intl = new MatPaginatorIntl();
  intl.itemsPerPageLabel = 'Filas por página:';
  intl.nextPageLabel = 'Página siguiente';
  intl.previousPageLabel = 'Página anterior';
  intl.firstPageLabel = 'Primera página';
  intl.lastPageLabel = 'Última página';
  intl.getRangeLabel = (page, pageSize, length) => {
    if (length === 0 || pageSize === 0) {
      return `0 de ${length}`;
    }
    const inicio = page * pageSize;
    const fin = Math.min(inicio + pageSize, length);
    return `${inicio + 1} – ${fin} de ${length}`;
  };
  return intl;
}
