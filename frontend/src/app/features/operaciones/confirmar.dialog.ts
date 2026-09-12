import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-confirmar',
  standalone: true,
  imports: [MatButtonModule, MatDialogModule],
  template: `
    <h2 mat-dialog-title>Iniciar un mes nuevo</h2>
    <mat-dialog-content>
      <p>Los minutos acumulados de todos los residentes volveran a cero.</p>
      <p>Las estancias ya registradas se conservan como historial y los vehiculos que estan
         dentro siguen dentro. Esta accion no se puede deshacer.</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="referencia.close(false)">Cancelar</button>
      <button mat-flat-button color="warn" (click)="referencia.close(true)">Si, iniciar mes</button>
    </mat-dialog-actions>
  `,
})
export class ConfirmarDialog {
  constructor(public referencia: MatDialogRef<ConfirmarDialog>) {}
}
