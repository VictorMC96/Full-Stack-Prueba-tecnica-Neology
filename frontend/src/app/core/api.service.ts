import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  ConsultaListado, DetalleVehiculo, Estancia, InformePagos, Mes, Pagina, TipoVehiculo, Vehiculo,
} from './modelos';

//Rutas-PorTipo
const RUTA_POR_TIPO: Record<TipoVehiculo, string> = {
  OFICIAL: 'oficiales',
  RESIDENTE: 'residentes',
  NO_RESIDENTE: 'no-residentes',
};

@Injectable({ providedIn: 'root' })
//Servicio-API
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly base = '/neo';

  //Consulta-Listado
  listarVehiculos(consulta: ConsultaListado): Observable<Pagina<Vehiculo>> {
    const params = new HttpParams()
      .set('placa', consulta.placa)
      .set('page', consulta.page)
      .set('size', consulta.size)
      .set('sort', consulta.sort)
      .set('direccion', consulta.direccion);
    return this.http.get<Pagina<Vehiculo>>(`${this.base}/vehiculos`, { params });
  }

  detalleVehiculo(placa: string): Observable<DetalleVehiculo> {
    return this.http.get<DetalleVehiculo>(`${this.base}/vehiculos/${encodeURIComponent(placa)}`);
  }

  //Alta-Vehiculo
  altaVehiculo(tipo: TipoVehiculo, datos: { placa: string; propietario: string }): Observable<Vehiculo> {
    return this.http.post<Vehiculo>(`${this.base}/vehiculos/${RUTA_POR_TIPO[tipo]}`, datos);
  }

  registrarEntrada(placa: string): Observable<Estancia> {
    return this.http.post<Estancia>(`${this.base}/estancias/entrada`, { placa });
  }

  registrarSalida(placa: string): Observable<Estancia> {
    return this.http.post<Estancia>(`${this.base}/estancias/salida`, { placa });
  }

  informePagos(): Observable<InformePagos> {
    return this.http.get<InformePagos>(`${this.base}/residentes/pagos`);
  }

  iniciarMes(): Observable<Mes> {
    return this.http.post<Mes>(`${this.base}/mes/iniciar`, {});
  }
}
