import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, throwError } from 'rxjs';

export const erroresInterceptor: HttpInterceptorFn = (peticion, siguiente) => {
  const aviso = inject(MatSnackBar);

  return siguiente(peticion).pipe(
    catchError((error: HttpErrorResponse) => {
      const mensaje = error.error?.mensaje ?? 'No se pudo contactar al servidor';
      aviso.open(mensaje, 'Cerrar', { duration: 6000, panelClass: 'aviso-error' });
      return throwError(() => error);
    }),
  );
};
