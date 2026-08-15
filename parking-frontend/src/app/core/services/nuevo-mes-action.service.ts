import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable, of } from 'rxjs';
import { finalize, switchMap } from 'rxjs/operators';
import {
  ConfirmDialogComponent,
} from '../../shared/components/confirm-dialog/confirm-dialog.component';
import { MesService } from './mes.service';

/**
 * Encapsula el flujo completo de "iniciar nuevo mes" (confirmación + llamada +
 * aviso de resultado) para no repetirlo en cada lugar de la UI que lo dispara
 * (toolbar y página de operaciones).
 */
@Injectable({ providedIn: 'root' })
export class NuevoMesActionService {
  constructor(
    private readonly dialog: MatDialog,
    private readonly mesService: MesService,
    private readonly snackBar: MatSnackBar,
  ) {}

  /** Pide confirmación y, si se acepta, ejecuta el reinicio de mes. Emite cuando termina el flujo. */
  ejecutar(marcarEnProceso: (enProceso: boolean) => void): Observable<void> {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      data: {
        titulo: 'Iniciar nuevo mes',
        mensaje:
          'Esto cerrará todas las estancias abiertas y reiniciará los tiempos y pagos acumulados de los residentes. ¿Deseas continuar?',
        textoConfirmar: 'Iniciar mes',
      },
    });

    return ref.afterClosed().pipe(
      switchMap((confirmado) => {
        if (!confirmado) {
          return of(undefined);
        }
        marcarEnProceso(true);
        return this.mesService.iniciarNuevoMes().pipe(
          finalize(() => marcarEnProceso(false)),
          switchMap(() => {
            this.snackBar.open('Nuevo mes iniciado correctamente.', 'Cerrar', { duration: 4000 });
            return of(undefined);
          }),
        );
      }),
    );
  }
}
