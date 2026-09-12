import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ApiService } from '../../core/api.service';
import { ConsultaListado, ETIQUETAS_TIPO, Vehiculo } from '../../core/modelos';

@Component({
  selector: 'app-listado-vehiculos',
  standalone: true,
  imports: [
    RouterLink, MatTableModule, MatPaginatorModule, MatSortModule, MatFormFieldModule,
    MatInputModule, MatIconModule, MatButtonModule, MatChipsModule, MatProgressBarModule,
  ],
  templateUrl: './listado-vehiculos.component.html',
  styleUrl: './listado-vehiculos.component.scss',
})
export class ListadoVehiculosComponent {
  private readonly api = inject(ApiService);

  readonly columnas = ['placa', 'tipo', 'propietario', 'estado', 'acumulado', 'acciones'];
  readonly etiquetas = ETIQUETAS_TIPO;

  readonly vehiculos = signal<Vehiculo[]>([]);
  readonly total = signal(0);
  readonly cargando = signal(false);

  readonly consulta = signal<ConsultaListado>({
    placa: '', page: 0, size: 10, sort: 'placa', direccion: 'asc',
  });

  constructor() {
    this.consultar();
  }

  consultar(): void {
    this.cargando.set(true);
    this.api.listarVehiculos(this.consulta()).subscribe({
      next: pagina => {
        this.vehiculos.set(pagina.contenido);
        this.total.set(pagina.total);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  filtrarPor(placa: string): void {
    this.consulta.update(actual => ({ ...actual, placa, page: 0 }));
    this.consultar();
  }

  cambiarPagina(evento: PageEvent): void {
    this.consulta.update(actual => ({ ...actual, page: evento.pageIndex, size: evento.pageSize }));
    this.consultar();
  }

  cambiarOrden(evento: Sort): void {
    const direccion = evento.direction === 'desc' ? 'desc' : 'asc';
    this.consulta.update(actual => ({ ...actual, sort: evento.active || 'placa', direccion }));
    this.consultar();
  }

  horas(minutos: number): string {
    const h = Math.floor(minutos / 60);
    const m = minutos % 60;
    return h > 0 ? `${h} h ${m} min` : `${m} min`;
  }
}
