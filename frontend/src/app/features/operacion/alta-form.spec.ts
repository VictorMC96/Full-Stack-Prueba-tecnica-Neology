import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatSelectHarness } from '@angular/material/select/testing';
import { MatSlideToggleHarness } from '@angular/material/slide-toggle/testing';
import { MatSnackBarHarness } from '@angular/material/snack-bar/testing';
import { VehiculoResponse } from '../../core/modelos/modelos';
import { AltaForm } from './alta-form';

function vehiculo(placa: string, tipo: VehiculoResponse['tipo']): VehiculoResponse {
  return { id: 3, placa, tipo, dentro: false, createdAt: '2026-09-30T10:00:00Z' };
}

describe('AltaForm', () => {
  let fixture: ComponentFixture<AltaForm>;
  let httpTesting: HttpTestingController;
  let loader: HarnessLoader;
  let rootLoader: HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AltaForm],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(AltaForm);
    loader = TestbedHarnessEnvironment.loader(fixture);
    rootLoader = TestbedHarnessEnvironment.documentRootLoader(fixture);
    await fixture.whenStable();
  });

  afterEach(() => httpTesting.verify());

  async function darDeAlta(placa: string): Promise<MatInputHarness> {
    const input = await loader.getHarness(MatInputHarness);
    await input.setValue(placa);
    const boton = await loader.getHarness(MatButtonHarness.with({ text: 'Dar de alta' }));
    await boton.click();
    return input;
  }

  it('con el toggle activado envía el alta y después la entrada, en ese orden', async () => {
    const toggle = await loader.getHarness(MatSlideToggleHarness);
    expect(await toggle.isChecked()).toBeTrue();
    expect(await toggle.getLabelText()).toBe('Registrar entrada ahora');
    const input = await darDeAlta('ofi-002');

    const alta = httpTesting.expectOne('/neo/vehiculos/oficiales');
    expect(alta.request.method).toBe('POST');
    expect(alta.request.body).toEqual({ placa: 'OFI002' });
    httpTesting.expectNone('/neo/estancias/entrada');
    alta.flush(vehiculo('OFI002', 'OFICIAL'), { status: 201, statusText: 'Created' });

    const entrada = httpTesting.expectOne('/neo/estancias/entrada');
    expect(entrada.request.method).toBe('POST');
    expect(entrada.request.body).toEqual({ placa: 'OFI002' });
    entrada.flush({}, { status: 201, statusText: 'Created' });
    await fixture.whenStable();

    const snackbar = await rootLoader.getHarness(MatSnackBarHarness);
    expect(await snackbar.getMessage()).toBe(
      'Vehículo OFI002 registrado como Oficial y entrada registrada',
    );
    expect(await input.getValue()).toBe('');
    expect(fixture.componentInstance.enviando()).toBeFalse();
    expect(await toggle.isChecked()).toBeTrue();
  });

  it('con el toggle desactivado envía solo el alta a /residentes', async () => {
    const select = await loader.getHarness(MatSelectHarness);
    await select.open();
    await select.clickOptions({ text: 'Residente' });
    const toggle = await loader.getHarness(MatSlideToggleHarness);
    await toggle.uncheck();
    await darDeAlta('RES009');

    const alta = httpTesting.expectOne('/neo/vehiculos/residentes');
    expect(alta.request.body).toEqual({ placa: 'RES009' });
    alta.flush(vehiculo('RES009', 'RESIDENTE'), { status: 201, statusText: 'Created' });
    await fixture.whenStable();

    httpTesting.expectNone('/neo/estancias/entrada');
    const snackbar = await rootLoader.getHarness(MatSnackBarHarness);
    expect(await snackbar.getMessage()).toBe('Vehículo RES009 registrado como Residente');
    expect(await toggle.isChecked()).toBeFalse();
  });

  it('si la entrada falla, el alta se da por hecha y el formulario se reinicia', async () => {
    const input = await darDeAlta('OFI003');
    httpTesting
      .expectOne('/neo/vehiculos/oficiales')
      .flush(vehiculo('OFI003', 'OFICIAL'), { status: 201, statusText: 'Created' });
    httpTesting
      .expectOne('/neo/estancias/entrada')
      .flush(
        { status: 409, detail: 'Ya está dentro', code: 'VEHICULO_YA_DENTRO' },
        { status: 409, statusText: 'Conflict' },
      );
    await fixture.whenStable();

    expect(await input.getValue()).toBe('');
    expect(fixture.componentInstance.enviando()).toBeFalse();
    expect(await rootLoader.getAllHarnesses(MatSnackBarHarness)).toEqual([]);
  });

  it('si el alta falla conserva el valor y no registra la entrada', async () => {
    const input = await darDeAlta('OFI004');
    httpTesting
      .expectOne('/neo/vehiculos/oficiales')
      .flush(
        { status: 409, detail: 'Ya registrado', code: 'VEHICULO_YA_REGISTRADO' },
        { status: 409, statusText: 'Conflict' },
      );
    await fixture.whenStable();
    httpTesting.expectNone('/neo/estancias/entrada');
    expect(await input.getValue()).toBe('OFI004');
    expect(fixture.componentInstance.enviando()).toBeFalse();
  });
});
