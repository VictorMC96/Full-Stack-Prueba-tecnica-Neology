package mx.neology.acceso.application.modelo;

/** Página, tamaño y orden pedidos por el cliente; el adaptador los traduce a su paginación. */
public record ConsultaPagina(int pagina, int tamano, String campoOrden, boolean ascendente) {}
