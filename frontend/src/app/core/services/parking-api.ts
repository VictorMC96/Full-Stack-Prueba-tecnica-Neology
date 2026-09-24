import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

import { Vehicle } from '../models/vehicle.model';
import { Stay } from '../models/stay.model';
import { ResidentPayment } from '../models/resident-payment.model';

@Injectable({
  providedIn: 'root'
})
export class ParkingApiService {

  private readonly apiUrl = `${environment.apiUrl}/neo`;

  constructor(
    private readonly http: HttpClient
  ) {}

  getVehicles(): Observable<Vehicle[]> {
    return this.http.get<Vehicle[]>(
      `${this.apiUrl}/vehiculos`
    );
  }

  getVehicle(plate: string): Observable<Vehicle> {
    return this.http.get<Vehicle>(
      `${this.apiUrl}/vehiculos/${encodeURIComponent(plate)}`
    );
  }

  registerOfficial(plate: string): Observable<Vehicle> {
    return this.http.post<Vehicle>(
      `${this.apiUrl}/vehiculos/oficiales`,
      { plate }
    );
  }

  registerResident(plate: string): Observable<Vehicle> {
    return this.http.post<Vehicle>(
      `${this.apiUrl}/vehiculos/residentes`,
      { plate }
    );
  }

  registerNonResident(plate: string): Observable<Vehicle> {
    return this.http.post<Vehicle>(
      `${this.apiUrl}/vehiculos/no-residentes`,
      { plate }
    );
  }

  registerEntry(plate: string): Observable<Stay> {
    return this.http.post<Stay>(
      `${this.apiUrl}/estancias/entrada`,
      { plate }
    );
  }

  registerExit(plate: string): Observable<Stay> {
    return this.http.post<Stay>(
      `${this.apiUrl}/estancias/salida`,
      { plate }
    );
  }

  getVehicleStays(plate: string): Observable<Stay[]> {
    return this.http.get<Stay[]>(
      `${this.apiUrl}/estancias/vehiculo/${encodeURIComponent(plate)}`
    );
  }

  getResidentPayments(): Observable<ResidentPayment[]> {
    return this.http.get<ResidentPayment[]>(
      `${this.apiUrl}/residentes/pagos`
    );
  }

  startNewMonth(): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(
      `${this.apiUrl}/mes/iniciar`,
      {}
    );
  }
}