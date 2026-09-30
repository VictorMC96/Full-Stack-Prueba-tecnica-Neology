import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EstanciasApi } from '../../core/api/estancias-api';
import { PlacaMayusculas } from '../../shared/placa/placa-mayusculas.directive';
import { normalizarPlaca, placaValidator } from '../../shared/placa/placa.validator';

@Component({
  selector: 'app-entrada-form',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    PlacaMayusculas,
  ],
  templateUrl: './entrada-form.html',
  styleUrl: './tarjeta-formulario.scss',
})
export class EntradaForm {
  private readonly api = inject(EstanciasApi);
  private readonly snackBar = inject(MatSnackBar);

  readonly form = new FormGroup({
    placa: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, placaValidator],
    }),
  });
  readonly enviando = signal(false);

  enviar(): void {
    if (this.form.invalid || this.enviando()) {
      this.form.markAllAsTouched();
      return;
    }
    const placa = normalizarPlaca(this.form.controls.placa.value);
    this.enviando.set(true);
    this.api.entrada(placa).subscribe({
      next: () => {
        this.enviando.set(false);
        this.snackBar.open(`Entrada registrada: ${placa}`, 'Cerrar', { duration: 4000 });
        this.form.reset();
      },
      error: () => this.enviando.set(false),
    });
  }
}
