import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatSnackBarHarness } from '@angular/material/snack-bar/testing';
import { EstanciaResponse, MomentoCobro } from '../../core/modelos/modelos';
import { MovimientoForm } from './movimiento-form';

function estancia(momentoCobro: MomentoCobro, importe: number | null): EstanciaResponse {
  return {
    id: 7,
    placa: 'NR0001',
    tipo: 'NO_RESIDENTE',
    entrada: '2026-09-30T10:00:00Z',
    salida: momentoCobro === 'NINGUNO' ? null : '2026-09-30T11:30:00Z',
    duracionSegundos: momentoCobro === 'NINGUNO' ? null : 5345,
    minutos: 90,
    importe,
    momentoCobro,
    periodoId: null,
  };
}

describe('MovimientoForm', () => {
  let fixture: ComponentFixture<MovimientoForm>;
  let httpTesting: HttpTestingController;
  let loader: HarnessLoader;
  let rootLoader: HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MovimientoForm],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(MovimientoForm);
    loader = TestbedHarnessEnvironment.loader(fixture);
    rootLoader = TestbedHarnessEnvironment.documentRootLoader(fixture);
    await fixture.whenStable();
  });

  afterEach(() => httpTesting.verify());

  const botonEntrada = () =>
    loader.getHarness(MatButtonHarness.with({ text: /Registrar entrada/ }));
  const botonSalida = () => loader.getHarness(MatButtonHarness.with({ text: /Registrar salida/ }));

  it('registra la entrada con la placa normalizada, avisa y limpia el formulario', async () => {
    const input = await loader.getHarness(MatInputHarness);
    await input.setValue('nr-0001');
    const entrada = await botonEntrada();
    expect(await entrada.isDisabled()).toBeFalse();
    await entrada.click();

    const req = httpTesting.expectOne('/neo/estancias/entrada');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ placa: 'NR0001' });
    expect(fixture.componentInstance.enviando()).toBe('entrada');
    expect(await (await botonSalida()).isDisabled()).toBeTrue();
    req.flush(estancia('NINGUNO', null), { status: 201, statusText: 'Created' });
    await fixture.whenStable();

    const snackbar = await rootLoader.getHarness(MatSnackBarHarness);
    expect(await snackbar.getMessage()).toBe('Entrada registrada: NR0001');
    expect(await input.getValue()).toBe('');
    expect(fixture.componentInstance.enviando()).toBeNull();
    expect((fixture.nativeElement as HTMLElement).querySelector('.resultado')).toBeNull();
  });

  it('Enter en el campo registra la entrada', async () => {
    const input = await loader.getHarness(MatInputHarness);
    await input.setValue('NR0001');
    const form = (fixture.nativeElement as HTMLElement).querySelector('form') as HTMLFormElement;
    form.requestSubmit();
    await fixture.whenStable();
    httpTesting.expectOne('/neo/estancias/entrada').flush(estancia('NINGUNO', null));
    await fixture.whenStable();
  });

  it('registra la salida y muestra placa, minutos, importe y cobro', async () => {
    const input = await loader.getHarness(MatInputHarness);
    await input.setValue('nr 0001');
    await (await botonSalida()).click();

    const req = httpTesting.expectOne('/neo/estancias/salida');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ placa: 'NR0001' });
    req.flush(estancia('AL_SALIR', 45));
    await fixture.whenStable();

    const resultado = (fixture.nativeElement as HTMLElement).querySelector('.resultado');
    expect(resultado?.querySelector('.placa')?.textContent).toBe('NR0001');
    expect(resultado?.textContent).toContain('89:05');
    expect(resultado?.textContent).toContain('Minutos cobrados');
    expect(resultado?.textContent).toContain('90');
    expect(resultado?.textContent).toContain('$45.00');
    expect(resultado?.textContent).toContain('Cobrar al salir');
    const snackbar = await rootLoader.getHarness(MatSnackBarHarness);
    expect(await snackbar.getMessage()).toBe(
      'Salida registrada: NR0001 · 89:05 · 90 min cobrados · $45.00 · Cobrar al salir',
    );
    expect(await input.getValue()).toBe('');
  });

  it('etiqueta el acumulado mensual y el sin cobro', async () => {
    const input = await loader.getHarness(MatInputHarness);
    for (const [momento, texto] of [
      ['INFORME_MENSUAL', 'Se acumula al informe mensual'],
      ['NINGUNO', 'Sin cobro'],
    ] as const) {
      await input.setValue('RES001');
      await (await botonSalida()).click();
      httpTesting.expectOne('/neo/estancias/salida').flush(estancia(momento, 0));
      await fixture.whenStable();
      expect(
        (fixture.nativeElement as HTMLElement).querySelector('.resultado')?.textContent,
      ).toContain(texto);
    }
  });

  it('con placa inválida deshabilita ambos botones y muestra el mat-error enlazado', async () => {
    expect(await (await botonEntrada()).isDisabled()).toBeTrue();
    expect(await (await botonSalida()).isDisabled()).toBeTrue();

    const input = await loader.getHarness(MatInputHarness);
    await input.setValue('A<');
    await input.blur();
    await fixture.whenStable();
    expect(await (await botonEntrada()).isDisabled()).toBeTrue();
    expect(await (await botonSalida()).isDisabled()).toBeTrue();
    const elemento = fixture.nativeElement as HTMLElement;
    const error = elemento.querySelector('mat-error');
    expect(error?.textContent).toContain('3 a 10 letras o dígitos');
    const describedBy = elemento.querySelector('input')?.getAttribute('aria-describedby') ?? '';
    expect(describedBy.split(' ')).toContain(error?.id ?? '');
  });

  it('en un 409 conserva la placa y rehabilita los botones', async () => {
    const input = await loader.getHarness(MatInputHarness);
    await input.setValue('NR0001');
    await (await botonEntrada()).click();
    httpTesting
      .expectOne('/neo/estancias/entrada')
      .flush(
        { status: 409, detail: 'Ya está dentro', code: 'VEHICULO_YA_DENTRO' },
        { status: 409, statusText: 'Conflict' },
      );
    await fixture.whenStable();
    expect(await input.getValue()).toBe('NR0001');
    expect(await (await botonEntrada()).isDisabled()).toBeFalse();
    expect(await (await botonSalida()).isDisabled()).toBeFalse();
    expect(fixture.componentInstance.enviando()).toBeNull();
  });
});
