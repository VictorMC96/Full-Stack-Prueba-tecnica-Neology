import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatSnackBarHarness } from '@angular/material/snack-bar/testing';
import { EstanciaResponse } from '../../core/modelos/modelos';
import { EntradaForm } from './entrada-form';

const ESTANCIA: EstanciaResponse = {
  id: 1,
  placa: 'ABC123',
  tipo: 'NO_RESIDENTE',
  entrada: '2026-09-30T10:00:00Z',
  salida: null,
  minutos: null,
  importe: null,
  momentoCobro: 'AL_SALIR',
  periodoId: null,
};

describe('EntradaForm', () => {
  let fixture: ComponentFixture<EntradaForm>;
  let httpTesting: HttpTestingController;
  let loader: HarnessLoader;
  let rootLoader: HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EntradaForm],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(EntradaForm);
    loader = TestbedHarnessEnvironment.loader(fixture);
    rootLoader = TestbedHarnessEnvironment.documentRootLoader(fixture);
    await fixture.whenStable();
  });

  afterEach(() => httpTesting.verify());

  it('envía la placa normalizada y muestra el snackbar', async () => {
    const input = await loader.getHarness(MatInputHarness);
    await input.setValue('abc-123');
    const boton = await loader.getHarness(MatButtonHarness.with({ text: 'Registrar entrada' }));
    expect(await boton.isDisabled()).toBeFalse();
    await boton.click();

    const req = httpTesting.expectOne('/neo/estancias/entrada');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ placa: 'ABC123' });
    expect(fixture.componentInstance.enviando()).toBeTrue();
    req.flush(ESTANCIA, { status: 201, statusText: 'Created' });
    await fixture.whenStable();

    const snackbar = await rootLoader.getHarness(MatSnackBarHarness);
    expect(await snackbar.getMessage()).toBe('Entrada registrada: ABC123');
    expect(await input.getValue()).toBe('');
    expect(fixture.componentInstance.enviando()).toBeFalse();
  });

  it('mantiene el botón deshabilitado y enlaza el mat-error con placa inválida', async () => {
    const input = await loader.getHarness(MatInputHarness);
    await input.setValue('A<');
    await input.blur();
    await fixture.whenStable();

    const boton = await loader.getHarness(MatButtonHarness.with({ text: 'Registrar entrada' }));
    expect(await boton.isDisabled()).toBeTrue();
    const elemento = fixture.nativeElement as HTMLElement;
    const error = elemento.querySelector('mat-error');
    expect(error?.textContent).toContain('3 a 10 letras o dígitos');
    const describedBy = elemento.querySelector('input')?.getAttribute('aria-describedby') ?? '';
    expect(describedBy.split(' ')).toContain(error?.id ?? '');
  });

  it('conserva el valor y rehabilita el botón cuando el backend falla', async () => {
    const input = await loader.getHarness(MatInputHarness);
    await input.setValue('ABC123');
    const boton = await loader.getHarness(MatButtonHarness.with({ text: 'Registrar entrada' }));
    await boton.click();
    httpTesting
      .expectOne('/neo/estancias/entrada')
      .flush(
        { status: 409, detail: 'Ya está dentro', code: 'VEHICULO_YA_DENTRO' },
        { status: 409, statusText: 'Conflict' },
      );
    await fixture.whenStable();
    expect(await input.getValue()).toBe('ABC123');
    expect(await boton.isDisabled()).toBeFalse();
  });
});
