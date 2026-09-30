import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MENSAJE_SIN_CONEXION, problemDetailInterceptor } from './problem-detail.interceptor';

describe('problemDetailInterceptor', () => {
  let http: HttpClient;
  let httpTesting: HttpTestingController;
  let snackBar: jasmine.SpyObj<MatSnackBar>;

  beforeEach(() => {
    snackBar = jasmine.createSpyObj<MatSnackBar>('MatSnackBar', ['open']);
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([problemDetailInterceptor])),
        provideHttpClientTesting(),
        { provide: MatSnackBar, useValue: snackBar },
      ],
    });
    http = TestBed.inject(HttpClient);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('muestra el detail del ProblemDetail y relanza el error', async () => {
    const llamada = http.get('/neo/vehiculos/XYZ').toPromise();
    httpTesting.expectOne('/neo/vehiculos/XYZ').flush(
      { status: 404, detail: 'No existe el vehículo XYZ', code: 'VEHICULO_NO_ENCONTRADO' },
      {
        status: 404,
        statusText: 'Not Found',
        headers: { 'Content-Type': 'application/problem+json' },
      },
    );
    await expectAsync(llamada).toBeRejected();
    expect(snackBar.open).toHaveBeenCalledWith(
      'No existe el vehículo XYZ',
      'Cerrar',
      jasmine.objectContaining({ duration: 6000 }),
    );
  });

  it('usa el primer errors[].message cuando no hay detail', async () => {
    const llamada = http.post('/neo/estancias/entrada', {}).toPromise();
    httpTesting.expectOne('/neo/estancias/entrada').flush(
      {
        status: 400,
        code: 'VALIDACION',
        errors: [{ field: 'placa', message: 'Placa inválida' }],
      },
      { status: 400, statusText: 'Bad Request' },
    );
    await expectAsync(llamada).toBeRejected();
    expect(snackBar.open).toHaveBeenCalledWith('Placa inválida', 'Cerrar', jasmine.anything());
  });

  it('muestra un mensaje genérico sin ProblemDetail', async () => {
    const llamada = http.get('/neo/vehiculos').toPromise();
    httpTesting.expectOne('/neo/vehiculos').error(new ProgressEvent('error'));
    await expectAsync(llamada).toBeRejected();
    expect(snackBar.open).toHaveBeenCalledWith(MENSAJE_SIN_CONEXION, 'Cerrar', jasmine.anything());
  });
});
