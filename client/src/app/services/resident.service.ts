import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../api-base-url';
import { ResidentPaymentResponse } from '../models/resident-payment.model';

const RESIDENTS_URL = `${API_BASE_URL}/neo/residentes`;

@Injectable({ providedIn: 'root' })
export class ResidentService {
  private readonly http = inject(HttpClient);

  getPayments(): Observable<ResidentPaymentResponse[]> {
    return this.http.get<ResidentPaymentResponse[]>(`${RESIDENTS_URL}/pagos`);
  }
}