import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../core/api.service';
import { ETIQUETAS_TIPO, TipoVehiculo } from '../../core/modelos';
import { ConfirmarDialog } from './confirmar.dialog';

export const PATRON_PLACA = /^[A-Za-z0-9-]{5,12}$/;

export interface TicketCobro {
  placa: string;
  minutos: number;
  tarifaInfo: string;
  total: number;
}

@Component({
  selector: 'app-operaciones',
  standalone: true,
  imports: [
    ReactiveFormsModule, DecimalPipe, MatTabsModule, MatCardModule, MatFormFieldModule, MatInputModule,
    MatSelectModule, MatButtonModule, MatIconModule, MatDialogModule,
  ],
  templateUrl: './operaciones.component.html',
  styleUrl: './operaciones.component.scss',
})
export class OperacionesComponent {
  private readonly api = inject(ApiService);
  private readonly fb = inject(FormBuilder);
  private readonly dialogo = inject(MatDialog);
  private readonly aviso = inject(MatSnackBar);

  readonly tipos: TipoVehiculo[] = ['OFICIAL', 'RESIDENTE', 'NO_RESIDENTE'];
  readonly etiquetas = ETIQUETAS_TIPO;
  readonly enviando = signal(false);
  readonly ticketCobro = signal<TicketCobro | null>(null);

  readonly formularioEntrada = this.fb.nonNullable.group({
    placa: ['', [Validators.required, Validators.pattern(PATRON_PLACA)]],
  });

  readonly formularioSalida = this.fb.nonNullable.group({
    placa: ['', [Validators.required, Validators.pattern(PATRON_PLACA)]],
  });

  readonly formularioAlta = this.fb.nonNullable.group({
    placa: ['', [Validators.required, Validators.pattern(PATRON_PLACA)]],
    propietario: ['', [Validators.maxLength(120)]],
    tipo: ['RESIDENTE' as TipoVehiculo, [Validators.required]],
  });

  registrarEntrada(): void {
    if (this.formularioEntrada.invalid) {
      this.formularioEntrada.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.api.registrarEntrada(this.formularioEntrada.getRawValue().placa).subscribe({
      next: estancia => {
        this.exito(`Entrada registrada para ${estancia.placa ?? 'el vehiculo'}`);
        this.formularioEntrada.reset();
      },
      error: () => this.enviando.set(false),
    });
  }

  registrarSalida(): void {
    if (this.formularioSalida.invalid) {
      this.formularioSalida.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    const placaEnviada = this.formularioSalida.getRawValue().placa;
    this.api.registrarSalida(placaEnviada).subscribe({
      next: estancia => {
        const monto = estancia.monto ?? 0;
        const minutos = estancia.minutos ?? 0;
        
        this.ticketCobro.set({
          placa: estancia.placa ?? placaEnviada,
          minutos,
          tarifaInfo: monto > 0 ? '$0.50/min (No Residente)' : '$0.00 (Oficial / Residente acumulado)',
          total: monto,
        });

        this.exito(`Salida registrada. Total a cobrar: $${monto.toFixed(2)}`);
        this.formularioSalida.reset();
      },
      error: () => this.enviando.set(false),
    });
  }

  darDeAlta(): void {
    if (this.formularioAlta.invalid) {
      this.formularioAlta.markAllAsTouched();
      return;
    }
    const { placa, propietario, tipo } = this.formularioAlta.getRawValue();
    this.enviando.set(true);
    this.api.altaVehiculo(tipo, { placa, propietario }).subscribe({
      next: () => {
        this.exito(`Vehiculo ${placa} dado de alta exitosamente`);
        this.formularioAlta.reset({ tipo: 'RESIDENTE' });
      },
      error: () => this.enviando.set(false),
    });
  }

  iniciarMes(): void {
    this.dialogo.open(ConfirmarDialog).afterClosed().subscribe(confirmado => {
      if (!confirmado) {
        return;
      }
      this.enviando.set(true);
      this.api.iniciarMes().subscribe({
        next: mes => this.exito(mes.mensaje),
        error: () => this.enviando.set(false),
      });
    });
  }

  cerrarTicket(): void {
    this.ticketCobro.set(null);
  }

  private exito(mensaje: string): void {
    this.enviando.set(false);
    this.aviso.open(mensaje, 'Cerrar', { duration: 5000 });
  }
}
