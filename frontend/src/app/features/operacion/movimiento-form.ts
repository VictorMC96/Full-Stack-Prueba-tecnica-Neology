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
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EstanciasApi } from '../../core/api/estancias-api';
import { EstanciaResponse } from '../../core/modelos/modelos';
import { ETIQUETA_COBRO, resumenSalida } from '../../shared/cobro/resumen-salida';
import { DuracionPipe } from '../../shared/duracion.pipe';
import { PlacaMayusculas } from '../../shared/placa/placa-mayusculas.directive';
import { normalizarPlaca, placaValidator } from '../../shared/placa/placa.validator';

export type Movimiento = 'entrada' | 'salida';

@Component({
  selector: 'app-movimiento-form',
  imports: [
    CurrencyPipe,
    DuracionPipe,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    PlacaMayusculas,
  ],
  templateUrl: './movimiento-form.html',
  styleUrl: './tarjeta-formulario.scss',
})
export class MovimientoForm {
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
  /** Movimiento cuya petición está en curso; `null` si no hay ninguna. */
  readonly enviando = signal<Movimiento | null>(null);
  /** Última salida registrada, para mostrar minutos, importe y cobro. */
  readonly resultado = signal<EstanciaResponse | null>(null);
  readonly etiquetas = ETIQUETA_COBRO;

  registrar(movimiento: Movimiento): void {
    if (this.form.invalid || this.enviando()) {
      this.form.markAllAsTouched();
      return;
    }
    const placa = normalizarPlaca(this.form.controls.placa.value);
    this.enviando.set(movimiento);
    const peticion = movimiento === 'entrada' ? this.api.entrada(placa) : this.api.salida(placa);
    peticion.subscribe({
      next: (estancia) => {
        this.enviando.set(null);
        if (movimiento === 'entrada') {
          this.resultado.set(null);
          this.snackBar.open(`Entrada registrada: ${placa}`, 'Cerrar', { duration: 4000 });
        } else {
          this.resultado.set(estancia);
          this.snackBar.open(resumenSalida(estancia, this.locale), 'Cerrar', { duration: 6000 });
        }
        this.formDir()?.resetForm();
      },
      error: () => this.enviando.set(null),
    });
  }
}
