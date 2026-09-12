import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { DetalleVehiculoComponent } from './detalle-vehiculo.component';
import { DetalleVehiculo } from '../../core/modelos';

describe('DetalleVehiculoComponent', () => {
  let http: HttpTestingController;

  const detalle: DetalleVehiculo = {
    vehiculo: {
      placa: 'RES100', tipo: 'RESIDENTE', propietario: 'Aldo',
      alta: '2026-09-11T10:00:00', dentro: false, minutosAcumulados: 130,
    },
    estancias: [
      { id: 1, placa: 'RES100', entrada: '2026-09-11T08:00:00', salida: '2026-09-11T10:10:00', minutos: 130, monto: 0, periodo: 1 },
    ],
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetalleVehiculoComponent],
      providers: [
        provideHttpClient(), provideHttpClientTesting(), provideRouter([]), provideNoopAnimations(),
        { provide: ActivatedRoute, useValue: { paramMap: of(new Map([['placa', 'RES100']])) } },
      ],
    }).compileComponents();
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('muestra los datos del vehiculo y su historial', () => {
    const fixture = TestBed.createComponent(DetalleVehiculoComponent);
    fixture.detectChanges();

    http.expectOne('/neo/vehiculos/RES100').flush(detalle);
    fixture.detectChanges();

    const texto = fixture.nativeElement.textContent;
    expect(texto).toContain('RES100');
    expect(texto).toContain('Aldo');
    expect(fixture.nativeElement.querySelectorAll('table tbody tr').length).toBe(1);
  });

  it('muestra el acumulado a cobrar solo para residentes', () => {
    const fixture = TestBed.createComponent(DetalleVehiculoComponent);
    fixture.detectChanges();

    http.expectOne('/neo/vehiculos/RES100').flush(detalle);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('6.50');
  });
});
