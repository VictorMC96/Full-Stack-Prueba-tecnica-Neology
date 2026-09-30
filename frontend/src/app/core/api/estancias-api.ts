import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { EstanciaResponse } from '../modelos/modelos';

@Injectable({ providedIn: 'root' })
export class EstanciasApi {
  private readonly http = inject(HttpClient);

  entrada(placa: string): Observable<EstanciaResponse> {
    return this.http.post<EstanciaResponse>('/neo/estancias/entrada', { placa });
  }

  salida(placa: string): Observable<EstanciaResponse> {
    return this.http.post<EstanciaResponse>('/neo/estancias/salida', { placa });
  }
}
