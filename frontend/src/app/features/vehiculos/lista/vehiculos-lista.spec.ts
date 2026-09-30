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
import { MatSnackBarHarness } from '@angular/material/snack-bar/testing';
import { Router, provideRouter } from '@angular/router';
import { EstanciaResponse, PageResponse, VehiculoResumen } from '../../../core/modelos/modelos';
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
    expect(req.request.params.get('sort')).toBe('dentro,desc');
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

  it('arranca ordenado por Estado (dentro primero) y al pulsar la cabecera alterna a dentro,asc', async () => {
    peticion().flush(pagina(VEHICULOS));
    await fixture.whenStable();

    const sort = await loader.getHarness(MatSortHarness);
    const activa = await sort.getActiveHeader();
    expect(await activa?.getLabel()).toBe('Estado');
    expect(await activa?.getSortDirection()).toBe('desc');

    await activa?.click();
    const req = peticion();
    expect(req.request.params.get('sort')).toBe('dentro,asc');
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

  describe('acción de estado', () => {
    let elemento: HTMLElement;
    let navegar: jasmine.Spy;

    const ESTANCIA: EstanciaResponse = {
      id: 9,
      placa: 'RES001',
      tipo: 'RESIDENTE',
      entrada: '2026-09-30T10:00:00Z',
      salida: '2026-09-30T10:12:00Z',
      duracionSegundos: 605,
      minutos: 11,
      importe: 5.5,
      momentoCobro: 'AL_SALIR',
      periodoId: null,
    };

    beforeEach(async () => {
      peticion().flush(pagina(VEHICULOS));
      await fixture.whenStable();
      elemento = fixture.nativeElement;
      navegar = spyOn(TestBed.inject(Router), 'navigate').and.resolveTo(true);
    });

    const boton = (aria: string): HTMLButtonElement =>
      elemento.querySelector(`[aria-label="${aria}"]`) as HTMLButtonElement;

    it('el chip Fuera registra la entrada, recarga la página y no navega', async () => {
      const chip = boton('Registrar entrada de OFI001');
      expect(chip.tagName).toBe('BUTTON');
      expect(chip.textContent).toContain('Fuera');
      chip.click();
      await fixture.whenStable();
      expect(chip.disabled).toBeTrue();

      const entrada = httpTesting.expectOne('/neo/estancias/entrada');
      expect(entrada.request.method).toBe('POST');
      expect(entrada.request.body).toEqual({ placa: 'OFI001' });
      entrada.flush(
        { ...ESTANCIA, placa: 'OFI001', salida: null },
        { status: 201, statusText: 'Created' },
      );

      const recarga = peticion();
      expect(recarga.request.params.get('page')).toBe('0');
      expect(recarga.request.params.get('size')).toBe('10');
      recarga.flush(pagina([{ ...VEHICULOS[0], dentro: true }, VEHICULOS[1]]));
      await fixture.whenStable();

      expect(navegar).not.toHaveBeenCalled();
      expect(boton('Registrar salida de OFI001')).not.toBeNull();
      const snackbar =
        await TestbedHarnessEnvironment.documentRootLoader(fixture).getHarness(MatSnackBarHarness);
      expect(await snackbar.getMessage()).toBe('Entrada registrada: OFI001');
    });

    it('el chip Dentro registra la salida y resume minutos, importe y cobro', async () => {
      const chip = boton('Registrar salida de RES001');
      expect(chip.textContent).toContain('Dentro');
      chip.click();
      await fixture.whenStable();

      const salida = httpTesting.expectOne('/neo/estancias/salida');
      expect(salida.request.method).toBe('POST');
      expect(salida.request.body).toEqual({ placa: 'RES001' });
      salida.flush(ESTANCIA);
      peticion().flush(pagina([VEHICULOS[0], { ...VEHICULOS[1], dentro: false }]));
      await fixture.whenStable();

      expect(navegar).not.toHaveBeenCalled();
      const snackbar =
        await TestbedHarnessEnvironment.documentRootLoader(fixture).getHarness(MatSnackBarHarness);
      expect(await snackbar.getMessage()).toBe(
        'Salida registrada: RES001 · 10:05 · 11 min cobrados · $5.50 · Cobrar al salir',
      );
    });

    it('si la petición falla rehabilita el chip y no recarga', async () => {
      const chip = boton('Registrar entrada de OFI001');
      chip.click();
      await fixture.whenStable();
      httpTesting
        .expectOne('/neo/estancias/entrada')
        .flush(
          { status: 409, detail: 'Ya está dentro', code: 'VEHICULO_YA_DENTRO' },
          { status: 409, statusText: 'Conflict' },
        );
      await fixture.whenStable();
      expect(chip.disabled).toBeFalse();
      httpTesting.expectNone((r) => r.url === '/neo/vehiculos');
      expect(navegar).not.toHaveBeenCalled();
    });

    it('el clic en la fila sigue llevando al detalle', async () => {
      (elemento.querySelector('tr[mat-row]') as HTMLElement).click();
      await fixture.whenStable();
      expect(navegar).toHaveBeenCalledWith(['/vehiculos', 'OFI001']);
    });
  });
});
