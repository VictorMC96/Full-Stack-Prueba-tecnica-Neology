import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { InformePagosResponse } from '../modelos/modelos';

export const MIME_XLSX = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';

@Injectable({ providedIn: 'root' })
export class InformeApi {
  private readonly http = inject(HttpClient);

  obtener(): Observable<InformePagosResponse> {
    return this.http.get<InformePagosResponse>('/neo/residentes/pagos', {
      headers: new HttpHeaders({ Accept: 'application/json' }),
    });
  }

  descargarExcel(): Observable<HttpResponse<Blob>> {
    return this.http.get('/neo/residentes/pagos', {
      headers: new HttpHeaders({ Accept: MIME_XLSX }),
      responseType: 'blob',
      observe: 'response',
    });
  }
}
