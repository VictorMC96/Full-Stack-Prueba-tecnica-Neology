import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../api-base-url';
import { PagedResponse } from '../models/paged-response.model';
import { Vehicle, VehicleRequest } from '../models/vehicle.model';

const VEHICLES_URL = `${API_BASE_URL}/neo/vehiculos`;

@Injectable({ providedIn: 'root' })
export class VehicleService {
  private readonly http = inject(HttpClient);

  getVehicles(plate: string, page: number, size: number): Observable<PagedResponse<Vehicle>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (plate) {
      params = params.set('plate', plate);
    }
    return this.http.get<PagedResponse<Vehicle>>(VEHICLES_URL, { params });
  }

  registerResident(body: VehicleRequest): Observable<void> {
    return this.http.post<void>(`${VEHICLES_URL}/residentes`, body);
  }

  registerOfficial(body: VehicleRequest): Observable<void> {
    return this.http.post<void>(`${VEHICLES_URL}/oficiales`, body);
  }

  registerNotResident(body: VehicleRequest): Observable<void> {
    return this.http.post<void>(`${VEHICLES_URL}/no-residentes`, body);
  }
}