import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, throwError } from 'rxjs';
import { ProblemDetail } from '../modelos/modelos';

export const MENSAJE_SIN_CONEXION = 'No se pudo conectar con el servidor';

export function mensajeDeError(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    const cuerpo = error.error as Partial<ProblemDetail> | null | undefined;
    if (cuerpo && typeof cuerpo === 'object') {
      if (typeof cuerpo.detail === 'string' && cuerpo.detail.trim()) {
        return cuerpo.detail;
      }
      const primero = cuerpo.errors?.[0];
      if (primero?.message) {
        return primero.message;
      }
    }
  }
  return MENSAJE_SIN_CONEXION;
}

export const problemDetailInterceptor: HttpInterceptorFn = (req, next) => {
  const snackBar = inject(MatSnackBar);
  return next(req).pipe(
    catchError((error: unknown) => {
      snackBar.open(mensajeDeError(error), 'Cerrar', { duration: 6000 });
      return throwError(() => error);
    }),
  );
};
