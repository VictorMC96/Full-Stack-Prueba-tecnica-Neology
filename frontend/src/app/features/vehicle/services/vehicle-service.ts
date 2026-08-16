import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map, Observable } from 'rxjs';

export interface VehicleStayDetail {
  licensePlate: string;
  type: string;
  brand: string;
  model: string;
  color: string;
  bodyType: string;
  year: number | null;
  countryPlate: string;
  entryDate: string;
  stayId: number;
}

interface ResponseApp<T> {
  code: number;
  status: string;
  message: string;
  timestamp: string;
  data: T;
}

export interface CreateVehicleDto {
  licensePlate: string;
  type: string;
  brand: string;
  model: string;
  color: string;
  bodyType: string;
  year: number | null;
  countryPlate: string;
}

export interface VehicleListItem {
  licensePlate: string;
  type: string;
  brand: string;
  model: string;
  color: string;
  bodyType: string;
  year: number | null;
  countryPlate: string;
  isActive: boolean;
}

export interface PageResult<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class VehicleService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/neo/vehicles';

  createVehicle(dto: CreateVehicleDto): Observable<unknown> {
    const path =
      dto.type === 'OFFICIAL' ? '/official'
      : dto.type === 'RESIDENT' ? '/resident'
      : '/no-resident';
    return this.http.post(this.baseUrl + path, dto);
  }

  getDetail(plate: string): Observable<VehicleStayDetail> {
    return this.http
      .get<ResponseApp<VehicleStayDetail>>(`${this.baseUrl}/${plate}/detail`)
      .pipe(map((res) => res.data));
  }

  getVehicles(page: number, size: number): Observable<PageResult<VehicleListItem>> {
    const params = { page, size };
    return this.http
      .get<ResponseApp<PageResult<VehicleListItem>>>(this.baseUrl, { params })
      .pipe(map((res) => res.data));
  }
}