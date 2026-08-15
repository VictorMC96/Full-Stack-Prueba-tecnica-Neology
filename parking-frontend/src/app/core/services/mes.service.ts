import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class MesService {
  private readonly baseUrl = `${environment.apiUrl}/mes`;

  constructor(private readonly http: HttpClient) {}

  iniciarNuevoMes(): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/iniciar`, {});
  }
}
