import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { forkJoin, map, Observable } from 'rxjs';

import { ParkingSpot } from '../components/parking-lot-spot/parking-lot-spot';

export interface CreateSpotDto {
  code: string;
  status: string;
}

// La forma en que el backend envuelve la respuesta
interface ResponseApp<T> {
  code: number;
  status: string;
  message: string;
  timestamp: string;
  data: T;
}

interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class ParkingLotService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/neo/spots';

  createSpot(dto: CreateSpotDto): Observable<unknown> {
    return this.http.post(this.baseUrl, dto);
  }

  createManySpots(dtos: CreateSpotDto[]): Observable<unknown[]> {
    return forkJoin(dtos.map((dto) => this.createSpot(dto)));
  }

  getSpots(): Observable<ParkingSpot[]> {
    const params = { page: 0, size: 100 };
    return this.http
      .get<ResponseApp<Page<ParkingSpot>>>(this.baseUrl, { params })
      .pipe(map((res) => res.data.content));
  }

  updateSpotStatus(id: number, status: string, licensePlate: string | null): Observable<unknown> {
    return this.http.patch(`${this.baseUrl}/${id}/status`, { status, licensePlate });
  }
}