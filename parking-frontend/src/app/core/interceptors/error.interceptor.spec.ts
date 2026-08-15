import { HttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ErrorInterceptor } from './error.interceptor';

describe('ErrorInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;

  beforeEach(() => {
    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: MatSnackBar, useValue: snackBarSpy },
        { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
      ],
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should show the backend message when the API returns a structured error', () => {
    http.get('/neo/vehiculos/NOPE').subscribe({ error: () => {} });

    httpMock
      .expectOne('/neo/vehiculos/NOPE')
      .flush(
        { message: 'No existe un vehículo registrado con la placa: NOPE' },
        { status: 404, statusText: 'Not Found' },
      );

    expect(snackBarSpy.open).toHaveBeenCalledWith(
      'No existe un vehículo registrado con la placa: NOPE',
      'Cerrar',
      { duration: 5000 },
    );
  });

  it('should show a connection message when the request fails with status 0', () => {
    http.get('/neo/vehiculos').subscribe({ error: () => {} });

    httpMock.expectOne('/neo/vehiculos').error(new ProgressEvent('error'), { status: 0 });

    expect(snackBarSpy.open).toHaveBeenCalledWith(
      'No se pudo conectar con el servidor. Verifica que el backend esté en ejecución.',
      'Cerrar',
      { duration: 5000 },
    );
  });
});
