import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { Router } from '@angular/router';
import { debounceTime, distinctUntilChanged, map } from 'rxjs';
import { VehiculosApi } from '../../../core/api/vehiculos-api';
import { PageResponse, VehiculoResumen } from '../../../core/modelos/modelos';
import { EmptyState } from '../../../shared/empty-state/empty-state';
import { EstadoChip } from '../../../shared/estado-chip/estado-chip';
import { Loading } from '../../../shared/loading/loading';
import { PlacaMayusculas } from '../../../shared/placa/placa-mayusculas.directive';
import { normalizarPlaca } from '../../../shared/placa/placa.validator';

export const ORDEN_INICIAL = 'placa,asc';

@Component({
  selector: 'app-vehiculos-lista',
  imports: [
    DatePipe,
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatPaginatorModule,
    MatSortModule,
    MatTableModule,
    EmptyState,
    EstadoChip,
    Loading,
    PlacaMayusculas,
  ],
  templateUrl: './vehiculos-lista.html',
  styleUrl: './vehiculos-lista.scss',
})
export class VehiculosLista {
  private readonly api = inject(VehiculosApi);
  private readonly router = inject(Router);

  readonly filtroControl = new FormControl('', { nonNullable: true });
  readonly columnas = ['placa', 'tipo', 'estado', 'createdAt', 'acciones'];
  readonly tamanos = [5, 10, 25];

  readonly pagina = signal(0);
  readonly tamano = signal(10);
  readonly orden = signal(ORDEN_INICIAL);
  readonly filtro = signal('');
  readonly datos = signal<PageResponse<VehiculoResumen> | null>(null);
  readonly cargando = signal(false);
  readonly error = signal(false);

  constructor() {
    this.filtroControl.valueChanges
      .pipe(debounceTime(300), map(normalizarPlaca), distinctUntilChanged(), takeUntilDestroyed())
      .subscribe((valor) => {
        this.filtro.set(valor);
        this.pagina.set(0);
        this.cargar();
      });
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.error.set(false);
    this.api
      .listar({
        pagina: this.pagina(),
        tamano: this.tamano(),
        orden: this.orden(),
        placa: this.filtro(),
      })
      .subscribe({
        next: (pagina) => {
          this.datos.set(pagina);
          this.cargando.set(false);
        },
        error: () => {
          this.error.set(true);
          this.cargando.set(false);
        },
      });
  }

  cambiarPagina(evento: PageEvent): void {
    this.pagina.set(evento.pageIndex);
    this.tamano.set(evento.pageSize);
    this.cargar();
  }

  cambiarOrden(orden: Sort): void {
    this.orden.set(orden.direction ? `${orden.active},${orden.direction}` : ORDEN_INICIAL);
    this.pagina.set(0);
    this.cargar();
  }

  limpiarFiltro(): void {
    this.filtroControl.setValue('');
  }

  verDetalle(placa: string): void {
    void this.router.navigate(['/vehiculos', placa]);
  }
}
