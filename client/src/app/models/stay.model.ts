export interface Stay {
  id: string;
  plate: string;
  entryDate: string;
  exitDate?: string;
}

export interface ExitResponse {
  plate: string;
  amountToPay: number;
}