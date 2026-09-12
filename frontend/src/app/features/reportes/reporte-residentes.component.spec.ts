import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { ReporteResidentesComponent } from './reporte-residentes.component';
import { InformePagos } from '../../core/modelos';

describe('ReporteResidentesComponent', () => {
  let http: HttpTestingController;

  const informe: InformePagos = {
    periodo: 3,
    renglones: [
      { placa: 'RES100', propietario: 'Aldo', minutos: 100, monto: 5 },
      { placa: 'RES200', propietario: 'Ana', minutos: 40, monto: 2 },
    ],
    total: 7,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReporteResidentesComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideNoopAnimations()],
    }).compileComponents();
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('pinta un renglon por residente y muestra el total', () => {
    const fixture = TestBed.createComponent(ReporteResidentesComponent);
    fixture.detectChanges();

    http.expectOne('/neo/residentes/pagos').flush(informe);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll('table tbody tr').length).toBe(2);
    expect(fixture.nativeElement.textContent).toContain('7.00');
    expect(fixture.nativeElement.textContent).toContain('Periodo 3');
  });

  it('muestra un estado vacio cuando no hay residentes', () => {
    const fixture = TestBed.createComponent(ReporteResidentesComponent);
    fixture.detectChanges();

    http.expectOne('/neo/residentes/pagos').flush({ periodo: 1, renglones: [], total: 0 });
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('No hay residentes');
  });
});
