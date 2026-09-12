//Modelos-API
//Tipos-Vehiculo
export type TipoVehiculo = 'OFICIAL' | 'RESIDENTE' | 'NO_RESIDENTE';

export interface Vehiculo {
  placa: string;
  tipo: TipoVehiculo;
  propietario: string | null;
  alta: string;
  dentro: boolean;
  minutosAcumulados: number;
}

export interface Estancia {
  id: number;
  placa: string;
  entrada: string;
  salida: string | null;
  minutos: number | null;
  monto: number | null;
  periodo: number;
}

export interface DetalleVehiculo {
  vehiculo: Vehiculo;
  estancias: Estancia[];
}

export interface Pagina<T> {
  contenido: T[];
  pagina: number;
  tamano: number;
  total: number;
  totalPaginas: number;
}

export interface RenglonPago {
  placa: string;
  propietario: string | null;
  minutos: number;
  monto: number;
}

export interface InformePagos {
  periodo: number;
  renglones: RenglonPago[];
  total: number;
}

export interface Mes {
  periodo: number;
  mensaje: string;
}

//Parametros-Listado
export interface ConsultaListado {
  placa: string;
  page: number;
  size: number;
  sort: string;
  direccion: 'asc' | 'desc';
}

//Etiquetas-Pantalla
export const ETIQUETAS_TIPO: Record<TipoVehiculo, string> = {
  OFICIAL: 'Oficial',
  RESIDENTE: 'Residente',
  NO_RESIDENTE: 'No residente',
};
