/** Refleja el ApiErrorResponse que devuelve el GlobalExceptionHandler del backend. */
export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  detalles: string[] | null;
}
