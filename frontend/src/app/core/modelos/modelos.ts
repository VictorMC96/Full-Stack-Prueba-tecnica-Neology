/** Código de tipo de vehículo (p. ej. `RESIDENTE`); los tipos se administran desde la interfaz. */
export type TipoVehiculo = string;
export type MomentoCobro = 'NINGUNO' | 'INFORME_MENSUAL' | 'AL_SALIR';
export type ModeloCobro = 'SIN_COBRO' | 'POR_MINUTO_AL_SALIR' | 'POR_MINUTO_ACUMULADO';

export interface TipoVehiculoResponse {
  codigo: string;
  nombre: string;
  modeloCobro: ModeloCobro;
  tarifaPorMinuto: number;
  provisional: boolean;
  integrado: boolean;
  vehiculos: number;
}

export interface TipoVehiculoAlta {
  codigo: string;
  nombre: string;
  modeloCobro: ModeloCobro;
  tarifaPorMinuto: number;
}

export type TipoVehiculoEdicion = Omit<TipoVehiculoAlta, 'codigo'>;

export interface VehiculoResponse {
  id: number;
  placa: string;
  tipo: TipoVehiculo;
  tipoNombre: string;
  dentro: boolean;
  createdAt: string;
}

export interface VehiculoResumen {
  placa: string;
  tipo: TipoVehiculo;
  tipoNombre: string;
  dentro: boolean;
  createdAt: string;
}

export interface EstanciaResponse {
  id: number;
  placa: string;
  tipo: TipoVehiculo;
  tipoNombre: string;
  entrada: string;
  salida: string | null;
  /** Segundos reales entre entrada y salida; null si la estancia sigue abierta. */
  duracionSegundos: number | null;
  /** Minutos cobrados: todo minuto iniciado cuenta completo. */
  minutos: number | null;
  importe: number | null;
  momentoCobro: MomentoCobro;
  periodoId: number | null;
}

export interface AcumuladoResponse {
  minutos: number;
  importe: number;
}

export interface VehiculoDetalleResponse {
  id: number;
  placa: string;
  tipo: TipoVehiculo;
  tipoNombre: string;
  dentro: boolean;
  estanciaAbierta: EstanciaResponse | null;
  acumuladoPeriodo: AcumuladoResponse | null;
  createdAt: string;
}

export interface PeriodoResponse {
  id: number;
  inicio: string;
  fin: string | null;
}

export interface FilaInformePagos {
  placa: string;
  minutos: number;
  importe: number;
}

export interface InformePagosResponse {
  periodo: PeriodoResponse;
  filas: FilaInformePagos[];
  totalMinutos: number;
  totalImporte: number;
}

export interface IniciarMesResponse {
  periodoAnterior: PeriodoResponse;
  periodoActual: PeriodoResponse;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface ErrorCampo {
  field: string;
  message: string;
}

export interface ProblemDetail {
  type?: string;
  title?: string;
  status: number;
  detail?: string;
  instance?: string;
  code?: string;
  errors?: ErrorCampo[];
}
