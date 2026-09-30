import { CurrencyPipe } from '@angular/common';
import { Component, LOCALE_ID, inject, signal, viewChild } from '@angular/core';
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
import { MatSnackBar } from '@angular/material/snack-bar';
import { EstanciasApi } from '../../core/api/estancias-api';
import { EstanciaResponse } from '../../core/modelos/modelos';
import { ETIQUETA_COBRO, resumenSalida } from '../../shared/cobro/resumen-salida';
import { PlacaMayusculas } from '../../shared/placa/placa-mayusculas.directive';
import { normalizarPlaca, placaValidator } from '../../shared/placa/placa.validator';

@Component({
  selector: 'app-salida-form',
  imports: [
    CurrencyPipe,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    PlacaMayusculas,
  ],
  templateUrl: './salida-form.html',
  styleUrl: './tarjeta-formulario.scss',
})
export class SalidaForm {
  private readonly api = inject(EstanciasApi);
  private readonly snackBar = inject(MatSnackBar);
  private readonly locale = inject(LOCALE_ID);
  private readonly formDir = viewChild(FormGroupDirective);

  readonly form = new FormGroup({
    placa: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, placaValidator],
    }),
  });
  readonly enviando = signal(false);
  readonly resultado = signal<EstanciaResponse | null>(null);
  readonly etiquetas = ETIQUETA_COBRO;

  enviar(): void {
    if (this.form.invalid || this.enviando()) {
      this.form.markAllAsTouched();
      return;
    }
    const placa = normalizarPlaca(this.form.controls.placa.value);
    this.enviando.set(true);
    this.api.salida(placa).subscribe({
      next: (estancia) => {
        this.enviando.set(false);
        this.resultado.set(estancia);
        this.snackBar.open(resumenSalida(estancia, this.locale), 'Cerrar', { duration: 6000 });
        this.formDir()?.resetForm();
      },
      error: () => this.enviando.set(false),
    });
  }
}
