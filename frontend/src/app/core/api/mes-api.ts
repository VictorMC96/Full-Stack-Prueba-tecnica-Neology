import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { IniciarMesResponse } from '../modelos/modelos';

@Injectable({ providedIn: 'root' })
export class MesApi {
  private readonly http = inject(HttpClient);

  iniciar(): Observable<IniciarMesResponse> {
    return this.http.post<IniciarMesResponse>('/neo/mes/iniciar', {});
  }
}
