import { Component, inject, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ApiService } from '../../core/api.service';
import { DetalleVehiculo, ETIQUETAS_TIPO } from '../../core/modelos';

const TARIFA_RESIDENTE = 0.05;

@Component({
  selector: 'app-detalle-vehiculo',
  standalone: true,
  imports: [
    DatePipe, DecimalPipe, RouterLink, MatCardModule, MatTableModule,
    MatButtonModule, MatIconModule, MatChipsModule, MatProgressBarModule,
  ],
  templateUrl: './detalle-vehiculo.component.html',
  styleUrl: './detalle-vehiculo.component.scss',
})
export class DetalleVehiculoComponent {
  private readonly api = inject(ApiService);
  private readonly ruta = inject(ActivatedRoute);

  readonly columnas = ['entrada', 'salida', 'minutos', 'monto', 'periodo'];
  readonly etiquetas = ETIQUETAS_TIPO;

  readonly detalle = signal<DetalleVehiculo | null>(null);
  readonly cargando = signal(true);

  constructor() {
    this.ruta.paramMap.subscribe(parametros => {
      const placa = parametros.get('placa') ?? '';
      this.cargando.set(true);
      this.api.detalleVehiculo(placa).subscribe({
        next: detalle => {
          this.detalle.set(detalle);
          this.cargando.set(false);
        },
        error: () => this.cargando.set(false),
      });
    });
  }

  acumuladoACobrar(): number {
    const actual = this.detalle();
    if (!actual || actual.vehiculo.tipo !== 'RESIDENTE') {
      return 0;
    }
    return actual.vehiculo.minutosAcumulados * TARIFA_RESIDENTE;
  }
}
