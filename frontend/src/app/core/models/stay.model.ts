export interface Stay {
  id: number;
  plate: string;
  entryDateTime: string;
  exitDateTime: string | null;
  durationMinutes: number | null;
  amount: number | null;
}