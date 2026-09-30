import { CurrencyPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, effect, inject, input, signal, untracked } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { VehiculosApi } from '../../../core/api/vehiculos-api';
import {
  EstanciaResponse,
  PageResponse,
  VehiculoDetalleResponse,
} from '../../../core/modelos/modelos';
import { EmptyState } from '../../../shared/empty-state/empty-state';
import { EstadoChip } from '../../../shared/estado-chip/estado-chip';
import { Loading } from '../../../shared/loading/loading';

@Component({
  selector: 'app-vehiculo-detalle',
  imports: [
    CurrencyPipe,
    DatePipe,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatPaginatorModule,
    MatTableModule,
    EmptyState,
    EstadoChip,
    Loading,
  ],
  templateUrl: './vehiculo-detalle.html',
  styleUrl: './vehiculo-detalle.scss',
})
export class VehiculoDetalle {
  private readonly api = inject(VehiculosApi);

  readonly placa = input.required<string>();

  readonly columnas = ['entrada', 'salida', 'minutos', 'importe'];
  readonly tamano = 10;

  readonly vehiculo = signal<VehiculoDetalleResponse | null>(null);
  readonly estancias = signal<PageResponse<EstanciaResponse> | null>(null);
  readonly pagina = signal(0);
  readonly cargando = signal(false);
  readonly cargandoEstancias = signal(false);
  readonly noEncontrado = signal(false);
  readonly error = signal(false);

  constructor() {
    effect(() => {
      const placa = this.placa();
      untracked(() => {
        this.pagina.set(0);
        this.cargar(placa);
      });
    });
  }

  cargar(placa: string = this.placa()): void {
    this.cargando.set(true);
    this.error.set(false);
    this.noEncontrado.set(false);
    this.api.detalle(placa).subscribe({
      next: (vehiculo) => {
        this.vehiculo.set(vehiculo);
        this.cargando.set(false);
      },
      error: (err: unknown) => {
        this.cargando.set(false);
        if (err instanceof HttpErrorResponse && err.status === 404) {
          this.noEncontrado.set(true);
        } else {
          this.error.set(true);
        }
      },
    });
    this.cargarEstancias(placa);
  }

  cargarEstancias(placa: string = this.placa()): void {
    this.cargandoEstancias.set(true);
    this.api.estancias(placa, this.pagina(), this.tamano).subscribe({
      next: (estancias) => {
        this.estancias.set(estancias);
        this.cargandoEstancias.set(false);
      },
      error: () => this.cargandoEstancias.set(false),
    });
  }

  cambiarPagina(evento: PageEvent): void {
    this.pagina.set(evento.pageIndex);
    this.cargarEstancias();
  }
}
