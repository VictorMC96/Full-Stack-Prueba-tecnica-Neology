import { Component, inject, signal, viewChild } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormGroupDirective,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { VehiculosApi } from '../../core/api/vehiculos-api';
import { TipoAlta } from '../../core/modelos/modelos';
import { PlacaMayusculas } from '../../shared/placa/placa-mayusculas.directive';
import { normalizarPlaca, placaValidator } from '../../shared/placa/placa.validator';

export const OPCIONES_TIPO: { valor: TipoAlta; etiqueta: string }[] = [
  { valor: 'OFICIAL', etiqueta: 'Oficial' },
  { valor: 'RESIDENTE', etiqueta: 'Residente' },
];

@Component({
  selector: 'app-alta-form',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    PlacaMayusculas,
  ],
  templateUrl: './alta-form.html',
  styleUrl: './tarjeta-formulario.scss',
})
export class AltaForm {
  private readonly api = inject(VehiculosApi);
  private readonly snackBar = inject(MatSnackBar);
  private readonly formDir = viewChild(FormGroupDirective);

  readonly opciones = OPCIONES_TIPO;
  readonly form = new FormGroup({
    placa: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, placaValidator],
    }),
    tipo: new FormControl<TipoAlta>('OFICIAL', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });
  readonly enviando = signal(false);

  enviar(): void {
    if (this.form.invalid || this.enviando()) {
      this.form.markAllAsTouched();
      return;
    }
    const placa = normalizarPlaca(this.form.controls.placa.value);
    const tipo = this.form.controls.tipo.value;
    const etiqueta = this.opciones.find((o) => o.valor === tipo)?.etiqueta ?? tipo;
    this.enviando.set(true);
    this.api.alta(tipo, placa).subscribe({
      next: (vehiculo) => {
        this.enviando.set(false);
        this.snackBar.open(`Vehículo ${vehiculo.placa} registrado como ${etiqueta}`, 'Cerrar', {
          duration: 4000,
        });
        this.formDir()?.resetForm({ placa: '', tipo });
      },
      error: () => this.enviando.set(false),
    });
  }
}
