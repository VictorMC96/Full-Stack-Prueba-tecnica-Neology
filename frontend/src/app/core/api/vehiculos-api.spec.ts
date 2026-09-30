import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { VehiculosApi } from './vehiculos-api';

describe('VehiculosApi', () => {
  let api: VehiculosApi;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    api = TestBed.inject(VehiculosApi);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('lista con página, tamaño, orden y filtro', () => {
    api.listar({ pagina: 1, tamano: 25, orden: 'tipo,desc', placa: 'res' }).subscribe();
    const req = httpTesting.expectOne((r) => r.url === '/neo/vehiculos');
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('page')).toBe('1');
    expect(req.request.params.get('size')).toBe('25');
    expect(req.request.params.get('sort')).toBe('tipo,desc');
    expect(req.request.params.get('placa')).toBe('res');
    req.flush({ content: [], page: 1, size: 25, totalElements: 0, totalPages: 0 });
  });

  it('omite placa cuando el filtro está vacío', () => {
    api.listar({ pagina: 0, tamano: 10, orden: 'placa,asc', placa: '' }).subscribe();
    const req = httpTesting.expectOne((r) => r.url === '/neo/vehiculos');
    expect(req.request.params.has('placa')).toBeFalse();
    req.flush({ content: [], page: 0, size: 10, totalElements: 0, totalPages: 0 });
  });

  it('pide detalle y estancias por placa', () => {
    api.detalle('ABC123').subscribe();
    httpTesting.expectOne('/neo/vehiculos/ABC123').flush({});
    api.estancias('ABC123', 2, 10).subscribe();
    const req = httpTesting.expectOne((r) => r.url === '/neo/vehiculos/ABC123/estancias');
    expect(req.request.params.get('page')).toBe('2');
    expect(req.request.params.get('size')).toBe('10');
    req.flush({ content: [], page: 2, size: 10, totalElements: 0, totalPages: 0 });
  });

  it('da de alta en la ruta del tipo', () => {
    api.alta('OFICIAL', 'OFI001').subscribe();
    const oficial = httpTesting.expectOne('/neo/vehiculos/oficiales');
    expect(oficial.request.method).toBe('POST');
    expect(oficial.request.body).toEqual({ placa: 'OFI001' });
    oficial.flush({});
    api.alta('RESIDENTE', 'RES001').subscribe();
    httpTesting.expectOne('/neo/vehiculos/residentes').flush({});
  });
});
