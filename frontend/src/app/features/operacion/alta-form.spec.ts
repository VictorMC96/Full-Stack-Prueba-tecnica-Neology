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
import { MatInputHarness } from '@angular/material/input/testing';
import { MatSelectHarness } from '@angular/material/select/testing';
import { MatSlideToggleHarness } from '@angular/material/slide-toggle/testing';
import { MatSnackBarHarness } from '@angular/material/snack-bar/testing';
import { TipoVehiculoResponse, VehiculoResponse } from '../../core/modelos/modelos';
import { AltaForm } from './alta-form';

const TIPOS: TipoVehiculoResponse[] = [
  {
    codigo: 'NO_RESIDENTE',
    nombre: 'No residente',
    modeloCobro: 'POR_MINUTO_AL_SALIR',
    tarifaPorMinuto: 0.5,
    provisional: true,
    integrado: true,
    vehiculos: 3,
  },
  {
    codigo: 'OFICIAL',
    nombre: 'Oficial',
    modeloCobro: 'SIN_COBRO',
    tarifaPorMinuto: 0,
    provisional: false,
    integrado: true,
    vehiculos: 2,
  },
  {
    codigo: 'RESIDENTE',
    nombre: 'Residente',
    modeloCobro: 'POR_MINUTO_ACUMULADO',
    tarifaPorMinuto: 0.05,
    provisional: false,
    integrado: true,
    vehiculos: 4,
  },
];

function vehiculo(placa: string, tipo: string, tipoNombre: string): VehiculoResponse {
  return { id: 3, placa, tipo, tipoNombre, dentro: false, createdAt: '2026-09-30T10:00:00Z' };
}

describe('AltaForm', () => {
  let fixture: ComponentFixture<AltaForm>;
  let httpTesting: HttpTestingController;
  let loader: HarnessLoader;
  let rootLoader: HarnessLoader;

  const tipos = (): TestRequest =>
    httpTesting.expectOne((r) => r.method === 'GET' && r.url === '/neo/tipos-vehiculo');

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

  it('llena el select desde la API y preselecciona el primer tipo no provisional', async () => {
    tipos().flush(TIPOS);
    await fixture.whenStable();
    const select = await loader.getHarness(MatSelectHarness);
    expect(await select.getValueText()).toBe('Oficial');
    await select.open();
    const opciones = await select.getOptions();
    const textos = await Promise.all(opciones.map((o) => o.getText()));
    expect(textos).toEqual(['No residente', 'Oficial', 'Residente']);
    await select.close();
  });

  it('con el toggle activado envía el alta genérica y después la entrada, en ese orden', async () => {
    tipos().flush(TIPOS);
    await fixture.whenStable();
    const toggle = await loader.getHarness(MatSlideToggleHarness);
    expect(await toggle.isChecked()).toBeTrue();
    expect(await toggle.getLabelText()).toBe('Registrar entrada ahora');
    const input = await darDeAlta('ofi-002');

    const alta = httpTesting.expectOne('/neo/vehiculos');
    expect(alta.request.method).toBe('POST');
    expect(alta.request.body).toEqual({ placa: 'OFI002', tipo: 'OFICIAL' });
    httpTesting.expectNone('/neo/estancias/entrada');
    alta.flush(vehiculo('OFI002', 'OFICIAL', 'Oficial'), { status: 201, statusText: 'Created' });

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
    expect(fixture.componentInstance.form.controls.tipo.value).toBe('OFICIAL');
  });

  it('con el toggle desactivado envía solo el alta con el tipo elegido', async () => {
    tipos().flush(TIPOS);
    await fixture.whenStable();
    const select = await loader.getHarness(MatSelectHarness);
    await select.open();
    await select.clickOptions({ text: 'Residente' });
    const toggle = await loader.getHarness(MatSlideToggleHarness);
    await toggle.uncheck();
    await darDeAlta('RES009');

    const alta = httpTesting.expectOne('/neo/vehiculos');
    expect(alta.request.body).toEqual({ placa: 'RES009', tipo: 'RESIDENTE' });
    alta.flush(vehiculo('RES009', 'RESIDENTE', 'Residente'), {
      status: 201,
      statusText: 'Created',
    });
    await fixture.whenStable();

    httpTesting.expectNone('/neo/estancias/entrada');
    const snackbar = await rootLoader.getHarness(MatSnackBarHarness);
    expect(await snackbar.getMessage()).toBe('Vehículo RES009 registrado como Residente');
    expect(await toggle.isChecked()).toBeFalse();
  });

  it('si la entrada falla, el alta se da por hecha y el formulario se reinicia', async () => {
    tipos().flush(TIPOS);
    await fixture.whenStable();
    const input = await darDeAlta('OFI003');
    httpTesting
      .expectOne('/neo/vehiculos')
      .flush(vehiculo('OFI003', 'OFICIAL', 'Oficial'), { status: 201, statusText: 'Created' });
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
    tipos().flush(TIPOS);
    await fixture.whenStable();
    const input = await darDeAlta('OFI004');
    httpTesting
      .expectOne('/neo/vehiculos')
      .flush(
        { status: 409, detail: 'Ya registrado', code: 'VEHICULO_YA_REGISTRADO' },
        { status: 409, statusText: 'Conflict' },
      );
    await fixture.whenStable();
    httpTesting.expectNone('/neo/estancias/entrada');
    expect(await input.getValue()).toBe('OFI004');
    expect(fixture.componentInstance.enviando()).toBeFalse();
  });

  it('sin tipos cargados mantiene el botón deshabilitado y permite reintentar', async () => {
    tipos().flush({ status: 500 }, { status: 500, statusText: 'Error' });
    await fixture.whenStable();
    const input = await loader.getHarness(MatInputHarness);
    await input.setValue('ABC123');
    const boton = await loader.getHarness(MatButtonHarness.with({ text: 'Dar de alta' }));
    expect(await boton.isDisabled()).toBeTrue();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain(
      'No se pudieron cargar los tipos',
    );
    await (await loader.getHarness(MatButtonHarness.with({ text: 'Reintentar' }))).click();
    tipos().flush(TIPOS);
    await fixture.whenStable();
    expect(await boton.isDisabled()).toBeFalse();
  });
});
