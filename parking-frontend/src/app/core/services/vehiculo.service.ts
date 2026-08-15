import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TipoVehiculo, Vehiculo, VehiculoDetalle } from '../models/vehiculo.model';

@Injectable({ providedIn: 'root' })
export class VehiculoService {
  private readonly baseUrl = `${environment.apiUrl}/vehiculos`;

  constructor(private readonly http: HttpClient) {}

  listar(): Observable<Vehiculo[]> {
    return this.http.get<Vehiculo[]>(this.baseUrl);
  }

  obtenerDetalle(placa: string): Observable<VehiculoDetalle> {
    return this.http.get<VehiculoDetalle>(`${this.baseUrl}/${encodeURIComponent(placa)}`);
  }

  altaOficial(placa: string): Observable<Vehiculo> {
    return this.http.post<Vehiculo>(`${this.baseUrl}/oficiales`, { placa });
  }

  altaResidente(placa: string): Observable<Vehiculo> {
    return this.http.post<Vehiculo>(`${this.baseUrl}/residentes`, { placa });
  }

  altaNoResidente(placa: string): Observable<Vehiculo> {
    return this.http.post<Vehiculo>(`${this.baseUrl}/no-residentes`, { placa });
  }

  altaPorTipo(placa: string, tipo: TipoVehiculo): Observable<Vehiculo> {
    switch (tipo) {
      case 'OFICIAL':
        return this.altaOficial(placa);
      case 'RESIDENTE':
        return this.altaResidente(placa);
      case 'NO_RESIDENTE':
        return this.altaNoResidente(placa);
    }
  }
}
