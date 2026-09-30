import { CurrencyPipe, DatePipe } from '@angular/common';
import { HttpResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { InformeApi } from '../../core/api/informe-api';
import { InformePagosResponse } from '../../core/modelos/modelos';
import { EmptyState } from '../../shared/empty-state/empty-state';
import { Loading } from '../../shared/loading/loading';

export const NOMBRE_ARCHIVO_POR_DEFECTO = 'informe-pagos.xlsx';

export function nombreDeArchivo(contentDisposition: string | null): string {
  const coincidencia = /filename\*?=(?:UTF-8'')?"?([^";]+)"?/i.exec(contentDisposition ?? '');
  const nombre = coincidencia?.[1]?.trim();
  return nombre ? decodeURIComponent(nombre) : NOMBRE_ARCHIVO_POR_DEFECTO;
}

@Component({
  selector: 'app-reporte',
  imports: [
    CurrencyPipe,
    DatePipe,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    EmptyState,
    Loading,
  ],
  templateUrl: './reporte.html',
  styleUrl: './reporte.scss',
})
export class Reporte {
  private readonly api = inject(InformeApi);

  readonly columnas = ['placa', 'minutos', 'importe'];
  readonly informe = signal<InformePagosResponse | null>(null);
  readonly cargando = signal(false);
  readonly error = signal(false);
  readonly descargando = signal(false);

  constructor() {
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.error.set(false);
    this.api.obtener().subscribe({
      next: (informe) => {
        this.informe.set(informe);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set(true);
        this.cargando.set(false);
      },
    });
  }

  descargar(): void {
    if (this.descargando()) {
      return;
    }
    this.descargando.set(true);
    this.api.descargarExcel().subscribe({
      next: (respuesta) => {
        this.descargando.set(false);
        this.guardar(respuesta);
      },
      error: () => this.descargando.set(false),
    });
  }

  private guardar(respuesta: HttpResponse<Blob>): void {
    if (!respuesta.body) {
      return;
    }
    const nombre = nombreDeArchivo(respuesta.headers.get('Content-Disposition'));
    const url = URL.createObjectURL(respuesta.body);
    const enlace = document.createElement('a');
    enlace.href = url;
    enlace.download = nombre;
    enlace.click();
    URL.revokeObjectURL(url);
  }
}
