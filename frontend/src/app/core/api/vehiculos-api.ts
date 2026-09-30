import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  EstanciaResponse,
  PageResponse,
  TipoVehiculo,
  VehiculoDetalleResponse,
  VehiculoResponse,
  VehiculoResumen,
} from '../modelos/modelos';

export interface ConsultaVehiculos {
  pagina: number;
  tamano: number;
  orden: string;
  placa?: string;
}

@Injectable({ providedIn: 'root' })
export class VehiculosApi {
  private readonly http = inject(HttpClient);

  listar(consulta: ConsultaVehiculos): Observable<PageResponse<VehiculoResumen>> {
    let params = new HttpParams()
      .set('page', consulta.pagina)
      .set('size', consulta.tamano)
      .set('sort', consulta.orden);
    if (consulta.placa) {
      params = params.set('placa', consulta.placa);
    }
    return this.http.get<PageResponse<VehiculoResumen>>('/neo/vehiculos', { params });
  }

  detalle(placa: string): Observable<VehiculoDetalleResponse> {
    return this.http.get<VehiculoDetalleResponse>(`/neo/vehiculos/${encodeURIComponent(placa)}`);
  }

  estancias(
    placa: string,
    pagina: number,
    tamano: number,
  ): Observable<PageResponse<EstanciaResponse>> {
    const params = new HttpParams().set('page', pagina).set('size', tamano);
    return this.http.get<PageResponse<EstanciaResponse>>(
      `/neo/vehiculos/${encodeURIComponent(placa)}/estancias`,
      { params },
    );
  }

  /** Alta genérica: el tipo es el código administrado en `/neo/tipos-vehiculo`. */
  alta(tipo: TipoVehiculo, placa: string): Observable<VehiculoResponse> {
    return this.http.post<VehiculoResponse>('/neo/vehiculos', { placa, tipo });
  }
}
