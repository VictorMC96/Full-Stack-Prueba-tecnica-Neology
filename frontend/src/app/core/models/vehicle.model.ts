export type VehicleType =
  | 'OFFICIAL'
  | 'RESIDENT'
  | 'NON_RESIDENT';

export interface Vehicle {
  id: number;
  plate: string;
  type: VehicleType;
}