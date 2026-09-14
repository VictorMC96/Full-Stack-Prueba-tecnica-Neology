import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../api-base-url';

const MONTH_URL = `${API_BASE_URL}/neo/mes`;

@Injectable({ providedIn: 'root' })
export class MonthService {
  private readonly http = inject(HttpClient);

  startNewMonth(): Observable<string> {
    return this.http.post(`${MONTH_URL}/iniciar`, null, { responseType: 'text' });
  }
}