import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ApiError } from '../models/api-error.model';

/**
 * Centraliza el manejo de errores HTTP: muestra el mensaje que envía el
 * GlobalExceptionHandler del backend (o uno genérico si no hay conexión)
 * en un snackbar, y deja que el componente decida si necesita hacer algo más.
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private readonly snackBar: MatSnackBar) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        const mensaje = this.obtenerMensaje(error);
        this.snackBar.open(mensaje, 'Cerrar', { duration: 5000 });
        return throwError(() => error);
      }),
    );
  }

  private obtenerMensaje(error: HttpErrorResponse): string {
    const apiError = error.error as ApiError | undefined;
    if (apiError?.message) {
      return apiError.message;
    }
    if (error.status === 0) {
      return 'No se pudo conectar con el servidor. Verifica que el backend esté en ejecución.';
    }
    return `Ocurrió un error inesperado (${error.status}).`;
  }
}
