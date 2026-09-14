export type VehicleType = 'RESIDENT' | 'NO_RESIDENT' | 'OFFICIALS';

export interface Vehicle {
  id: string;
  plate: string;
  type: VehicleType;
}

export interface VehicleRequest {
  plate: string;
}