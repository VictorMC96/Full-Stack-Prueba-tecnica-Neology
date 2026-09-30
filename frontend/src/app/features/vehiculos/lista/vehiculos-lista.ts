import { DatePipe } from '@angular/common';
import { Component, LOCALE_ID, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router } from '@angular/router';
import { debounceTime, distinctUntilChanged, map } from 'rxjs';
import { EstanciasApi } from '../../../core/api/estancias-api';
import { VehiculosApi } from '../../../core/api/vehiculos-api';
import { PageResponse, VehiculoResumen } from '../../../core/modelos/modelos';
import { resumenSalida } from '../../../shared/cobro/resumen-salida';
import { EmptyState } from '../../../shared/empty-state/empty-state';
import { EstadoChip } from '../../../shared/estado-chip/estado-chip';
import { Loading } from '../../../shared/loading/loading';
import { PlacaMayusculas } from '../../../shared/placa/placa-mayusculas.directive';
import { normalizarPlaca } from '../../../shared/placa/placa.validator';

/** Dentro primero y, en cada grupo, placa ascendente (el backend desempata por placa). */
export const ORDEN_INICIAL = 'dentro,desc';

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
    MatTooltipModule,
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
  private readonly estancias = inject(EstanciasApi);
  private readonly snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);
  private readonly locale = inject(LOCALE_ID);

  readonly filtroControl = new FormControl('', { nonNullable: true });
  readonly columnas = ['placa', 'tipo', 'dentro', 'createdAt', 'acciones'];
  readonly tamanos = [5, 10, 25];

  readonly pagina = signal(0);
  readonly tamano = signal(10);
  readonly orden = signal(ORDEN_INICIAL);
  readonly filtro = signal('');
  readonly datos = signal<PageResponse<VehiculoResumen> | null>(null);
  readonly cargando = signal(false);
  readonly error = signal(false);
  /** Placa cuya entrada o salida se está registrando desde el listado. */
  readonly placaEnCurso = signal<string | null>(null);

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

  /** Registra la entrada (si está fuera) o la salida (si está dentro) y recarga la página actual. */
  alternarEstado(vehiculo: VehiculoResumen, evento: Event): void {
    evento.stopPropagation();
    if (this.placaEnCurso()) {
      return;
    }
    this.placaEnCurso.set(vehiculo.placa);
    const peticion = vehiculo.dentro
      ? this.estancias.salida(vehiculo.placa)
      : this.estancias.entrada(vehiculo.placa);
    peticion.subscribe({
      next: (estancia) => {
        this.placaEnCurso.set(null);
        const mensaje = vehiculo.dentro
          ? resumenSalida(estancia, this.locale)
          : `Entrada registrada: ${estancia.placa}`;
        this.snackBar.open(mensaje, 'Cerrar', { duration: 6000 });
        this.cargar();
      },
      error: () => this.placaEnCurso.set(null),
    });
  }
}
