import { Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { TipoVehiculoEdicion, TipoVehiculoResponse } from '../../core/modelos/modelos';
import { OPCIONES_MODELO_COBRO } from '../../shared/cobro/modelo-cobro';
import { camposTipo, enlazarTarifaAlModelo } from './tipo-form';

@Component({
  selector: 'app-tipo-dialog',
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './tipo-dialog.html',
  styles: `
    .campos {
      display: flex;
      flex-direction: column;
      gap: var(--app-space-2);
      padding-top: var(--app-space-2);
    }
    .campo {
      width: 100%;
    }
  `,
})
export class TipoDialog {
  readonly tipo = inject<TipoVehiculoResponse>(MAT_DIALOG_DATA);
  private readonly dialogRef = inject<MatDialogRef<TipoDialog, TipoVehiculoEdicion>>(MatDialogRef);

  readonly modelos = OPCIONES_MODELO_COBRO;
  readonly form = new FormGroup(camposTipo(this.tipo));

  constructor() {
    enlazarTarifaAlModelo(this.form);
    if (this.tipo.integrado) {
      this.form.controls.modeloCobro.disable();
    }
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { nombre, modeloCobro, tarifaPorMinuto } = this.form.getRawValue();
    this.dialogRef.close({ nombre: nombre.trim(), modeloCobro, tarifaPorMinuto });
  }
}
