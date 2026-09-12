import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ApiService } from './api.service';
import { Pagina, Vehiculo } from './modelos';

describe('ApiService', () => {
  let servicio: ApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ApiService, provideHttpClient(), provideHttpClientTesting()],
    });
    servicio = TestBed.inject(ApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('pide el listado con filtro, pagina, tamano y orden', () => {
    const respuesta: Pagina<Vehiculo> = {
      contenido: [], pagina: 0, tamano: 10, total: 0, totalPaginas: 0,
    };

    servicio.listarVehiculos({ placa: 'ABC', page: 1, size: 25, sort: 'placa', direccion: 'desc' })
      .subscribe(resultado => expect(resultado.tamano).toBe(10));

    const peticion = http.expectOne(
      r => r.url === '/neo/vehiculos'
        && r.params.get('placa') === 'ABC'
        && r.params.get('page') === '1'
        && r.params.get('size') === '25'
        && r.params.get('direccion') === 'desc');
    expect(peticion.request.method).toBe('GET');
    peticion.flush(respuesta);
  });

  it('da de alta un oficial en su propia ruta', () => {
    servicio.altaVehiculo('OFICIAL', { placa: 'ABC-123', propietario: 'Gobierno' }).subscribe();

    const peticion = http.expectOne('/neo/vehiculos/oficiales');
    expect(peticion.request.method).toBe('POST');
    expect(peticion.request.body.placa).toBe('ABC-123');
    peticion.flush({});
  });

  it('da de alta un residente y un no residente en las rutas que les tocan', () => {
    servicio.altaVehiculo('RESIDENTE', { placa: 'RES-001', propietario: 'Aldo' }).subscribe();
    http.expectOne('/neo/vehiculos/residentes').flush({});

    servicio.altaVehiculo('NO_RESIDENTE', { placa: 'VIS-001', propietario: 'Visita' }).subscribe();
    http.expectOne('/neo/vehiculos/no-residentes').flush({});
  });

  it('registra entrada y salida', () => {
    servicio.registrarEntrada('ABC-123').subscribe();
    const entrada = http.expectOne('/neo/estancias/entrada');
    expect(entrada.request.body.placa).toBe('ABC-123');
    entrada.flush({});

    servicio.registrarSalida('ABC-123').subscribe();
    http.expectOne('/neo/estancias/salida').flush({});
  });

  it('pide el informe de pagos y el inicio de mes', () => {
    servicio.informePagos().subscribe();
    http.expectOne('/neo/residentes/pagos').flush({ periodo: 1, renglones: [], total: 0 });

    servicio.iniciarMes().subscribe();
    const peticion = http.expectOne('/neo/mes/iniciar');
    expect(peticion.request.method).toBe('POST');
    peticion.flush({ periodo: 2, mensaje: 'ok' });
  });
});
