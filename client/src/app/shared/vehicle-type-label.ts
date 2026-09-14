import { VehicleType } from '../models/vehicle.model';

export function vehicleTypeLabel(type: VehicleType): string {
  switch (type) {
    case 'RESIDENT': return 'Resident';
    case 'NO_RESIDENT': return 'Non-resident';
    case 'OFFICIALS': return 'Official';
  }
}