export type TipoVehiculo = 'OFICIAL' | 'RESIDENTE' | 'NO_RESIDENTE';

export interface Vehiculo {
  placa: string;
  tipo: TipoVehiculo;
}

export interface Estancia {
  id: number;
  placa: string;
  fechaEntrada: string;
  fechaSalida: string | null;
  cobro: number;
}

export interface VehiculoDetalle {
  placa: string;
  tipo: TipoVehiculo;
  estancias: Estancia[];
  tiempoAcumuladoMinutos: number | null;
  pagoAcumulado: number | null;
}
