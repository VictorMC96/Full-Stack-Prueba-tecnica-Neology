import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { ListadoVehiculosComponent } from './listado-vehiculos.component';
import { Pagina, Vehiculo } from '../../core/modelos';

describe('ListadoVehiculosComponent', () => {
  let http: HttpTestingController;

  const pagina: Pagina<Vehiculo> = {
    contenido: [
      { placa: 'ABC123', tipo: 'RESIDENTE', propietario: 'Aldo', alta: '2026-09-11T10:00:00', dentro: true, minutosAcumulados: 120 },
      { placa: 'OFI100', tipo: 'OFICIAL', propietario: 'Patrulla', alta: '2026-09-11T10:00:00', dentro: false, minutosAcumulados: 0 },
    ],
    pagina: 0, tamano: 10, total: 2, totalPaginas: 1,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListadoVehiculosComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([]), provideNoopAnimations()],
    }).compileComponents();
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('pinta un renglon por cada vehiculo recibido', () => {
    const fixture = TestBed.createComponent(ListadoVehiculosComponent);
    fixture.detectChanges();

    http.expectOne(r => r.url === '/neo/vehiculos').flush(pagina);
    fixture.detectChanges();

    const renglones = fixture.nativeElement.querySelectorAll('table tbody tr');
    expect(renglones.length).toBe(2);
    expect(fixture.nativeElement.textContent).toContain('ABC123');
    expect(fixture.nativeElement.textContent).toContain('Residente');
  });

  it('muestra el estado vacio cuando no hay vehiculos', () => {
    const fixture = TestBed.createComponent(ListadoVehiculosComponent);
    fixture.detectChanges();

    http.expectOne(r => r.url === '/neo/vehiculos')
      .flush({ contenido: [], pagina: 0, tamano: 10, total: 0, totalPaginas: 0 });
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('No hay vehiculos');
  });

  it('vuelve a consultar al escribir un filtro de placa', () => {
    const fixture = TestBed.createComponent(ListadoVehiculosComponent);
    fixture.detectChanges();
    http.expectOne(r => r.url === '/neo/vehiculos').flush(pagina);

    fixture.componentInstance.filtrarPor('OFI');
    fixture.detectChanges();

    const peticion = http.expectOne(r => r.url === '/neo/vehiculos' && r.params.get('placa') === 'OFI');
    expect(peticion.request.params.get('page')).toBe('0');
    peticion.flush(pagina);
  });
});
