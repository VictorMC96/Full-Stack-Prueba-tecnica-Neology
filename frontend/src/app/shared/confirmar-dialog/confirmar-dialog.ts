import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';

export interface ConfirmarDialogDatos {
  titulo: string;
  texto: string;
  confirmar: string;
  cancelar: string;
}

@Component({
  selector: 'app-confirmar-dialog',
  imports: [MatDialogModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>{{ datos.titulo }}</h2>
    <mat-dialog-content>
      <p>{{ datos.texto }}</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button matButton type="button" [mat-dialog-close]="false" cdkFocusInitial>
        {{ datos.cancelar }}
      </button>
      <button matButton="filled" type="button" [mat-dialog-close]="true">
        {{ datos.confirmar }}
      </button>
    </mat-dialog-actions>
  `,
})
export class ConfirmarDialog {
  readonly datos = inject<ConfirmarDialogDatos>(MAT_DIALOG_DATA);
}
