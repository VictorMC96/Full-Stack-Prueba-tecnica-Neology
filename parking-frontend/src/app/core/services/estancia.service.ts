import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Estancia } from '../models/vehiculo.model';

@Injectable({ providedIn: 'root' })
export class EstanciaService {
  private readonly baseUrl = `${environment.apiUrl}/estancias`;

  constructor(private readonly http: HttpClient) {}

  registrarEntrada(placa: string): Observable<Estancia> {
    return this.http.post<Estancia>(`${this.baseUrl}/entrada`, { placa });
  }

  registrarSalida(placa: string): Observable<Estancia> {
    return this.http.post<Estancia>(`${this.baseUrl}/salida`, { placa });
  }
}
