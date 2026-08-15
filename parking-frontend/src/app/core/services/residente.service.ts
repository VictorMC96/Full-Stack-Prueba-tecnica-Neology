import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ResidentePago } from '../models/residente.model';

@Injectable({ providedIn: 'root' })
export class ResidenteService {
  private readonly baseUrl = `${environment.apiUrl}/residentes`;

  constructor(private readonly http: HttpClient) {}

  informePagos(): Observable<ResidentePago[]> {
    return this.http.get<ResidentePago[]>(`${this.baseUrl}/pagos`);
  }
}
