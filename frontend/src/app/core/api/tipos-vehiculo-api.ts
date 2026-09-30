import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { TipoVehiculoAlta, TipoVehiculoEdicion, TipoVehiculoResponse } from '../modelos/modelos';

@Injectable({ providedIn: 'root' })
export class TiposVehiculoApi {
  private readonly http = inject(HttpClient);

  listar(): Observable<TipoVehiculoResponse[]> {
    return this.http.get<TipoVehiculoResponse[]>('/neo/tipos-vehiculo');
  }

  crear(datos: TipoVehiculoAlta): Observable<TipoVehiculoResponse> {
    return this.http.post<TipoVehiculoResponse>('/neo/tipos-vehiculo', datos);
  }

  actualizar(codigo: string, datos: TipoVehiculoEdicion): Observable<TipoVehiculoResponse> {
    return this.http.put<TipoVehiculoResponse>(
      `/neo/tipos-vehiculo/${encodeURIComponent(codigo)}`,
      datos,
    );
  }

  eliminar(codigo: string): Observable<void> {
    return this.http.delete<void>(`/neo/tipos-vehiculo/${encodeURIComponent(codigo)}`);
  }
}
