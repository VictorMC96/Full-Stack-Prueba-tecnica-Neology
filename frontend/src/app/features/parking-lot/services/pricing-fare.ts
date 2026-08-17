export type VehicleType = 'OFFICIAL' | 'RESIDENT' | 'NO_RESIDENT';

type FareFn = (minutes: number) => number;

const FARES: Record<string, FareFn> = {
  OFFICIAL: () => 0,
  RESIDENT: (minutes) => minutes * 0.05,
  NO_RESIDENT: (minutes) => minutes * 0.5,
};

export function calculateFare(type: string, entryDate: string): number {
  const fareFn = FARES[type];
  if (!fareFn) {
    throw new Error(`No hay tarifa definida para el tipo: ${type}`);
  }
  const entry = new Date(entryDate).getTime();
  const now = Date.now();
  const minutes = Math.max(0, Math.floor((now - entry) / 60000));
  return fareFn(minutes);
}

export function elapsedMinutes(entryDate: string): number {
  const entry = new Date(entryDate).getTime();
  return Math.max(0, Math.floor((Date.now() - entry) / 60000));
}