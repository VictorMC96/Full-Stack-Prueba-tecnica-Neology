import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
  TestRequest,
} from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatPaginatorHarness } from '@angular/material/paginator/testing';
import { MatSortHarness } from '@angular/material/sort/testing';
import { MatTableHarness } from '@angular/material/table/testing';
import { provideRouter } from '@angular/router';
import { PageResponse, VehiculoResumen } from '../../../core/modelos/modelos';
import { VehiculosLista } from './vehiculos-lista';

const VEHICULOS: VehiculoResumen[] = [
  { placa: 'OFI001', tipo: 'OFICIAL', dentro: false, createdAt: '2026-09-01T10:00:00Z' },
  { placa: 'RES001', tipo: 'RESIDENTE', dentro: true, createdAt: '2026-09-02T10:00:00Z' },
];

function pagina(content: VehiculoResumen[], total = content.length): PageResponse<VehiculoResumen> {
  return { content, page: 0, size: 10, totalElements: total, totalPages: 1 };
}

describe('VehiculosLista', () => {
  let fixture: ComponentFixture<VehiculosLista>;
  let httpTesting: HttpTestingController;
  let loader: HarnessLoader;

  const peticion = (): TestRequest => httpTesting.expectOne((r) => r.url === '/neo/vehiculos');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VehiculosLista],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(VehiculosLista);
    loader = TestbedHarnessEnvironment.loader(fixture);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('pide la primera página y pinta una fila por vehículo con chips', async () => {
    const req = peticion();
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('page')).toBe('0');
    expect(req.request.params.get('size')).toBe('10');
    expect(req.request.params.get('sort')).toBe('placa,asc');
    expect(req.request.params.has('placa')).toBeFalse();
    req.flush(pagina(VEHICULOS, 42));
    await fixture.whenStable();

    const tabla = await loader.getHarness(MatTableHarness);
    expect((await tabla.getRows()).length).toBe(2);
    const elemento = fixture.nativeElement as HTMLElement;
    expect(elemento.querySelectorAll('h1').length).toBe(1);
    expect(elemento.querySelector('mat-label')?.textContent).toContain('Filtrar por placa');
    expect(elemento.querySelectorAll('td .placa').length).toBe(2);
    const chips = Array.from(elemento.querySelectorAll('td .chip')).map((c) =>
      c.textContent?.trim(),
    );
    expect(chips).toEqual(['Oficial', 'Fuera', 'Residente', 'Dentro']);
    expect(elemento.querySelector('[aria-label="Ver detalle de RES001"]')).not.toBeNull();
  });

  it('cambia de página y de tamaño en el servidor', async () => {
    peticion().flush(pagina(VEHICULOS, 42));
    await fixture.whenStable();

    const paginador = await loader.getHarness(MatPaginatorHarness);
    await paginador.goToNextPage();
    const req = peticion();
    expect(req.request.params.get('page')).toBe('1');
    expect(req.request.params.get('size')).toBe('10');
    req.flush({ ...pagina(VEHICULOS, 42), page: 1 });
    await fixture.whenStable();

    await paginador.setPageSize(25);
    const req2 = peticion();
    expect(req2.request.params.get('size')).toBe('25');
    req2.flush({ ...pagina(VEHICULOS, 42), size: 25 });
    await fixture.whenStable();
  });

  it('ordena en el servidor al pulsar una cabecera', async () => {
    peticion().flush(pagina(VEHICULOS));
    await fixture.whenStable();

    const sort = await loader.getHarness(MatSortHarness);
    const cabeceras = await sort.getSortHeaders({ label: 'Tipo' });
    await cabeceras[0].click();
    const req = peticion();
    expect(req.request.params.get('sort')).toBe('tipo,asc');
    expect(req.request.params.get('page')).toBe('0');
    req.flush(pagina(VEHICULOS));
    await fixture.whenStable();
  });

  it('filtra por placa normalizada tras 300 ms', async () => {
    peticion().flush(pagina(VEHICULOS));
    await fixture.whenStable();

    jasmine.clock().install();
    jasmine.clock().mockDate();
    try {
      const input = (fixture.nativeElement as HTMLElement).querySelector(
        'input',
      ) as HTMLInputElement;
      input.value = 're';
      input.dispatchEvent(new Event('input'));
      input.value = 'res-0';
      input.dispatchEvent(new Event('input'));
      jasmine.clock().tick(299);
      httpTesting.expectNone((r) => r.url === '/neo/vehiculos');
      jasmine.clock().tick(1);
      const req = peticion();
      expect(req.request.params.get('placa')).toBe('RES0');
      expect(req.request.params.get('page')).toBe('0');
      req.flush(pagina([VEHICULOS[1]]));
    } finally {
      jasmine.clock().uninstall();
    }
    await fixture.whenStable();
    expect(fixture.componentInstance.filtro()).toBe('RES0');
  });

  it('muestra el estado vacío sin filtro', async () => {
    peticion().flush(pagina([]));
    await fixture.whenStable();
    const elemento = fixture.nativeElement as HTMLElement;
    expect(elemento.textContent).toContain('No hay vehículos');
    expect(elemento.textContent).not.toContain('Limpiar filtro');
  });

  it('muestra el estado vacío con filtro y permite limpiarlo', async () => {
    peticion().flush(pagina(VEHICULOS));
    await fixture.whenStable();
    fixture.componentInstance.filtro.set('ZZZ');
    fixture.componentInstance.filtroControl.setValue('ZZZ', { emitEvent: false });
    fixture.componentInstance.cargar();
    peticion().flush(pagina([]));
    await fixture.whenStable();

    const elemento = fixture.nativeElement as HTMLElement;
    expect(elemento.textContent).toContain('Ningún vehículo coincide con «ZZZ»');
    const limpiar = await loader.getHarness(MatButtonHarness.with({ text: 'Limpiar filtro' }));
    expect(limpiar).toBeTruthy();
  });

  it('muestra el error y reintenta', async () => {
    peticion().flush(
      { status: 500, detail: 'Fallo' },
      { status: 500, statusText: 'Internal Server Error' },
    );
    await fixture.whenStable();
    const elemento = fixture.nativeElement as HTMLElement;
    expect(elemento.textContent).toContain('No se pudo cargar el listado');
    expect(elemento.querySelector('app-loading')).toBeNull();

    const reintentar = await loader.getHarness(MatButtonHarness.with({ text: 'Reintentar' }));
    await reintentar.click();
    peticion().flush(pagina(VEHICULOS));
    await fixture.whenStable();
    expect(elemento.textContent).not.toContain('No se pudo cargar el listado');
    expect(elemento.querySelectorAll('tr[mat-row]').length).toBe(2);
  });
});
