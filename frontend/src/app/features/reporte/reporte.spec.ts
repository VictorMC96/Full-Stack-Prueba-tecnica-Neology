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
import { MIME_XLSX } from '../../core/api/informe-api';
import { InformePagosResponse } from '../../core/modelos/modelos';
import { nombreDeArchivo, Reporte } from './reporte';

const INFORME: InformePagosResponse = {
  periodo: { id: 1, inicio: '2026-09-01T00:00:00Z', fin: null },
  filas: [
    { placa: 'RES001', minutos: 120, importe: 6 },
    { placa: 'RES002', minutos: 30, importe: 1.5 },
  ],
  totalMinutos: 150,
  totalImporte: 7.5,
};

describe('nombreDeArchivo', () => {
  it('extrae el nombre de Content-Disposition o usa el valor por defecto', () => {
    expect(nombreDeArchivo('attachment; filename="informe-pagos-2026-09.xlsx"')).toBe(
      'informe-pagos-2026-09.xlsx',
    );
    expect(nombreDeArchivo('attachment; filename=informe.xlsx')).toBe('informe.xlsx');
    expect(nombreDeArchivo(null)).toBe('informe-pagos.xlsx');
  });
});

describe('Reporte', () => {
  let fixture: ComponentFixture<Reporte>;
  let httpTesting: HttpTestingController;
  let loader: HarnessLoader;

  const peticionJson = (): TestRequest =>
    httpTesting.expectOne(
      (r) => r.url === '/neo/residentes/pagos' && r.headers.get('Accept') === 'application/json',
    );

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Reporte],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(Reporte);
    loader = TestbedHarnessEnvironment.loader(fixture);
  });

  afterEach(() => httpTesting.verify());

  it('pide el informe en JSON y pinta filas, total y periodo bajo un único h1', async () => {
    peticionJson().flush(INFORME);
    await fixture.whenStable();
    const elemento = fixture.nativeElement as HTMLElement;
    expect(elemento.querySelectorAll('h1').length).toBe(1);
    expect(elemento.textContent).toContain('Periodo iniciado el');
    expect(elemento.querySelectorAll('tr[mat-row]').length).toBe(2);
    expect(elemento.querySelectorAll('td .placa').length).toBe(2);
    const pie = elemento.querySelector('tr[mat-footer-row]');
    expect(pie?.textContent).toContain('Total');
    expect(pie?.textContent).toContain('150');
    expect(pie?.textContent).toContain('$7.50');
  });

  it('descarga el Excel con el nombre de Content-Disposition', async () => {
    peticionJson().flush(INFORME);
    await fixture.whenStable();
    spyOn(URL, 'createObjectURL').and.returnValue('blob:informe');
    spyOn(URL, 'revokeObjectURL');
    let nombreDescargado = '';
    spyOn(HTMLAnchorElement.prototype, 'click').and.callFake(function (this: HTMLAnchorElement) {
      nombreDescargado = this.download;
    });

    const boton = await loader.getHarness(MatButtonHarness.with({ text: /Descargar Excel/ }));
    expect(await boton.isDisabled()).toBeFalse();
    await boton.click();
    const req = httpTesting.expectOne(
      (r) => r.url === '/neo/residentes/pagos' && r.headers.get('Accept') === MIME_XLSX,
    );
    expect(req.request.responseType).toBe('blob');
    req.flush(new Blob(['xlsx'], { type: MIME_XLSX }), {
      headers: { 'Content-Disposition': 'attachment; filename="informe-pagos-2026-09.xlsx"' },
    });
    await fixture.whenStable();
    expect(nombreDescargado).toBe('informe-pagos-2026-09.xlsx');
    expect(URL.createObjectURL).toHaveBeenCalled();
    expect(URL.revokeObjectURL).toHaveBeenCalledWith('blob:informe');
  });

  it('usa informe-pagos.xlsx sin cabecera Content-Disposition', async () => {
    peticionJson().flush(INFORME);
    await fixture.whenStable();
    spyOn(URL, 'createObjectURL').and.returnValue('blob:informe');
    spyOn(URL, 'revokeObjectURL');
    let nombreDescargado = '';
    spyOn(HTMLAnchorElement.prototype, 'click').and.callFake(function (this: HTMLAnchorElement) {
      nombreDescargado = this.download;
    });
    fixture.componentInstance.descargar();
    httpTesting
      .expectOne((r) => r.headers.get('Accept') === MIME_XLSX)
      .flush(new Blob(['xlsx'], { type: MIME_XLSX }));
    await fixture.whenStable();
    expect(nombreDescargado).toBe('informe-pagos.xlsx');
  });

  it('muestra el vacío y deshabilita la descarga sin filas', async () => {
    peticionJson().flush({ ...INFORME, filas: [], totalMinutos: 0, totalImporte: 0 });
    await fixture.whenStable();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain(
      'No hay residentes registrados',
    );
    const boton = await loader.getHarness(MatButtonHarness.with({ text: /Descargar Excel/ }));
    expect(await boton.isDisabled()).toBeTrue();
  });

  it('muestra el error y reintenta', async () => {
    peticionJson().flush({ status: 500 }, { status: 500, statusText: 'Error' });
    await fixture.whenStable();
    const elemento = fixture.nativeElement as HTMLElement;
    expect(elemento.textContent).toContain('No se pudo cargar el informe');
    const reintentar = await loader.getHarness(MatButtonHarness.with({ text: 'Reintentar' }));
    await reintentar.click();
    peticionJson().flush(INFORME);
    await fixture.whenStable();
    expect(elemento.textContent).not.toContain('No se pudo cargar el informe');
  });
});
