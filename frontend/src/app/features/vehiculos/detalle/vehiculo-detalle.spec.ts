import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
  TestRequest,
} from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatPaginatorHarness } from '@angular/material/paginator/testing';
import { provideRouter } from '@angular/router';
import {
  EstanciaResponse,
  PageResponse,
  VehiculoDetalleResponse,
} from '../../../core/modelos/modelos';
import { VehiculoDetalle } from './vehiculo-detalle';

const ESTANCIA: EstanciaResponse = {
  id: 1,
  placa: 'RES001',
  tipo: 'RESIDENTE',
  entrada: '2026-09-10T08:00:00Z',
  salida: '2026-09-10T09:00:00Z',
  minutos: 60,
  importe: 3,
  momentoCobro: 'INFORME_MENSUAL',
  periodoId: 1,
};

const RESIDENTE: VehiculoDetalleResponse = {
  id: 1,
  placa: 'RES001',
  tipo: 'RESIDENTE',
  dentro: true,
  estanciaAbierta: { ...ESTANCIA, id: 2, salida: null, minutos: null, importe: null },
  acumuladoPeriodo: { minutos: 120, importe: 6 },
  createdAt: '2026-09-01T10:00:00Z',
};

function pagina(
  content: EstanciaResponse[],
  total = content.length,
): PageResponse<EstanciaResponse> {
  return { content, page: 0, size: 10, totalElements: total, totalPages: 1 };
}

describe('VehiculoDetalle', () => {
  let fixture: ComponentFixture<VehiculoDetalle>;
  let httpTesting: HttpTestingController;
  let loader: HarnessLoader;

  const detalle = (): TestRequest => httpTesting.expectOne('/neo/vehiculos/RES001');
  const estancias = (): TestRequest =>
    httpTesting.expectOne((r) => r.url === '/neo/vehiculos/RES001/estancias');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VehiculoDetalle],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(VehiculoDetalle);
    fixture.componentRef.setInput('placa', 'RES001');
    loader = TestbedHarnessEnvironment.loader(fixture);
    await fixture.whenStable();
  });

  afterEach(() => httpTesting.verify());

  it('pide detalle y estancias y pinta cabecera, acumulado y estancia abierta', async () => {
    const reqEstancias = estancias();
    expect(reqEstancias.request.params.get('page')).toBe('0');
    expect(reqEstancias.request.params.get('size')).toBe('10');
    detalle().flush(RESIDENTE);
    reqEstancias.flush(pagina([ESTANCIA], 15));
    await fixture.whenStable();

    const elemento = fixture.nativeElement as HTMLElement;
    expect(elemento.querySelectorAll('h1').length).toBe(1);
    expect(elemento.querySelector('h1 .placa')?.textContent).toBe('RES001');
    const chips = Array.from(elemento.querySelectorAll('.chips .chip')).map((c) =>
      c.textContent?.trim(),
    );
    expect(chips).toEqual(['Residente', 'Dentro']);
    expect(elemento.textContent).toContain('Acumulado del periodo');
    expect(elemento.textContent).toContain('120');
    expect(elemento.textContent).toContain('$6.00');
    expect(elemento.textContent).toContain('Estancia abierta');
    expect(elemento.querySelectorAll('tr[mat-row]').length).toBe(1);
  });

  it('no muestra el acumulado ni la estancia abierta cuando llegan en null', async () => {
    detalle().flush({
      ...RESIDENTE,
      tipo: 'OFICIAL',
      dentro: false,
      estanciaAbierta: null,
      acumuladoPeriodo: null,
    });
    estancias().flush(pagina([]));
    await fixture.whenStable();
    const elemento = fixture.nativeElement as HTMLElement;
    expect(elemento.textContent).not.toContain('Acumulado del periodo');
    expect(elemento.textContent).not.toContain('Estancia abierta');
    expect(elemento.textContent).toContain('Este vehículo aún no tiene estancias');
  });

  it('pide la página nueva del historial al paginar', async () => {
    detalle().flush(RESIDENTE);
    estancias().flush(pagina([ESTANCIA], 25));
    await fixture.whenStable();
    const paginador = await loader.getHarness(MatPaginatorHarness);
    await paginador.goToNextPage();
    const req = estancias();
    expect(req.request.params.get('page')).toBe('1');
    req.flush({ ...pagina([ESTANCIA], 25), page: 1 });
    await fixture.whenStable();
  });

  it('muestra "Vehículo no encontrado" con enlace al listado en un 404', async () => {
    detalle().flush(
      { status: 404, detail: 'No existe', code: 'VEHICULO_NO_ENCONTRADO' },
      { status: 404, statusText: 'Not Found' },
    );
    estancias().flush(
      { status: 404, detail: 'No existe', code: 'VEHICULO_NO_ENCONTRADO' },
      { status: 404, statusText: 'Not Found' },
    );
    await fixture.whenStable();
    const elemento = fixture.nativeElement as HTMLElement;
    expect(elemento.textContent).toContain('Vehículo no encontrado');
    expect(elemento.querySelector('app-empty-state a')?.getAttribute('href')).toBe('/vehiculos');
    expect(elemento.querySelector('mat-card')).toBeNull();
    expect(elemento.querySelectorAll('h1').length).toBe(1);
  });

  it('muestra el error genérico con Reintentar', async () => {
    detalle().flush({ status: 500 }, { status: 500, statusText: 'Error' });
    estancias().flush(pagina([]));
    await fixture.whenStable();
    const elemento = fixture.nativeElement as HTMLElement;
    expect(elemento.textContent).toContain('No se pudo cargar el vehículo');
    (elemento.querySelector('.estado-error button') as HTMLButtonElement).click();
    detalle().flush(RESIDENTE);
    estancias().flush(pagina([ESTANCIA]));
    await fixture.whenStable();
    expect(elemento.textContent).not.toContain('No se pudo cargar el vehículo');
  });
});
