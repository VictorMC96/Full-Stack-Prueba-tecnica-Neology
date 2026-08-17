import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export interface CreatePaymentDto {
  stayId: number;
  amount: number;
  status: string;
}

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/neo/payments';

  createPayment(dto: CreatePaymentDto): Observable<unknown> {
    return this.http.post(this.baseUrl, dto);
  }
}