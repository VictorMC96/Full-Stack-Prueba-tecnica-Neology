import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../api-base-url';
import { PagedResponse } from '../models/paged-response.model';
import { ExitResponse, Stay } from '../models/stay.model';
import { VehicleRequest } from '../models/vehicle.model';

const STAYS_URL = `${API_BASE_URL}/neo/estancias`;

@Injectable({ providedIn: 'root' })
export class StayService {
  private readonly http = inject(HttpClient);

  // GET /neo/estancias?plate=&page=&size=
  getStays(plate: string, page: number, size: number): Observable<PagedResponse<Stay>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (plate) {
      params = params.set('plate', plate);
    }
    return this.http.get<PagedResponse<Stay>>(STAYS_URL, { params });
  }

  registerEntry(body: VehicleRequest): Observable<void> {
    return this.http.post<void>(`${STAYS_URL}/entrada`, body);
  }

  registerExit(body: VehicleRequest): Observable<ExitResponse> {
    return this.http.post<ExitResponse>(`${STAYS_URL}/salida`, body);
  }
}