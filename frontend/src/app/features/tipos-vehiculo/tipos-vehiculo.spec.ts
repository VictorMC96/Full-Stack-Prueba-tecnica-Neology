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
import { MATERIAL_ANIMATIONS } from '@angular/material/core';
import { MatDialogHarness } from '@angular/material/dialog/testing';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatSelectHarness } from '@angular/material/select/testing';
import { MatSnackBarHarness } from '@angular/material/snack-bar/testing';
import { provideRouter } from '@angular/router';
import { TipoVehiculoResponse } from '../../core/modelos/modelos';
import { TiposVehiculo } from './tipos-vehiculo';

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
    codigo: 'PROVEEDOR',
    nombre: 'Proveedor',
    modeloCobro: 'POR_MINUTO_AL_SALIR',
    tarifaPorMinuto: 0.25,
    provisional: false,
    integrado: false,
    vehiculos: 1,
  },
  {
    codigo: 'VISITA',
    nombre: 'Visita',
    modeloCobro: 'POR_MINUTO_ACUMULADO',
    tarifaPorMinuto: 0.1,
    provisional: false,
    integrado: false,
    vehiculos: 0,
  },
];

describe('TiposVehiculo', () => {
  let fixture: ComponentFixture<TiposVehiculo>;
  let httpTesting: HttpTestingController;
  let loader: HarnessLoader;
  let rootLoader: HarnessLoader;

  const listado = (): TestRequest =>
    httpTesting.expectOne((r) => r.method === 'GET' && r.url === '/neo/tipos-vehiculo');
  const elemento = (): HTMLElement => fixture.nativeElement as HTMLElement;
  const boton = (aria: string): HTMLButtonElement =>
    elemento().querySelector(`[aria-label="${aria}"]`) as HTMLButtonElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TiposVehiculo],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: MATERIAL_ANIMATIONS, useValue: { animationsDisabled: true } },
      ],
    }).compileComponents();
    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(TiposVehiculo);
    loader = TestbedHarnessEnvironment.loader(fixture);
    rootLoader = TestbedHarnessEnvironment.documentRootLoader(fixture);
    await fixture.whenStable();
  });

  afterEach(() => httpTesting.verify());

  it('carga los tipos y pinta código, nombre, modelo, tarifa, vehículos y chips', async () => {
    listado().flush(TIPOS);
    await fixture.whenStable();

    const el = elemento();
    expect(el.querySelectorAll('h1').length).toBe(1);
    expect(el.querySelector('h1')?.textContent).toContain('Tipos de vehículo');
    const filas = Array.from(el.querySelectorAll('tr[mat-row]'));
    expect(filas.length).toBe(4);
    const celdas = Array.from(filas[0].querySelectorAll('td')).map((td) => td.textContent?.trim());
    expect(filas[0].querySelector('td .placa')?.textContent).toBe('NO_RESIDENTE');
    expect(celdas[1]).toBe('No residente');
    expect(celdas[2]).toBe('Por minuto al salir');
    expect(celdas[3]).toBe('$0.50');
    expect(celdas[4]).toBe('3');
    const chips = Array.from(filas[0].querySelectorAll('.chip')).map((c) => c.textContent?.trim());
    expect(chips).toEqual(['Integrado', 'Provisional']);
    expect(Array.from(filas[3].querySelectorAll('td'))[2].textContent?.trim()).toBe(
      'Por minuto, acumulado al informe',
    );
    expect(filas[1].querySelectorAll('.chip').length).toBe(1);
  });

  it('crea un tipo y recarga el listado', async () => {
    listado().flush(TIPOS);
    await fixture.whenStable();

    const codigo = await loader.getHarness(
      MatInputHarness.with({ selector: '[formControlName="codigo"]' }),
    );
    await codigo.setValue('mensajeria');
    const nombre = await loader.getHarness(
      MatInputHarness.with({ selector: '[formControlName="nombre"]' }),
    );
    await nombre.setValue('Mensajería');
    const tarifa = await loader.getHarness(
      MatInputHarness.with({ selector: '[formControlName="tarifaPorMinuto"]' }),
    );
    await tarifa.setValue('0.35');
    const crear = await loader.getHarness(MatButtonHarness.with({ text: /Crear tipo/ }));
    expect(await crear.isDisabled()).toBeFalse();
    await crear.click();

    const req = httpTesting.expectOne({ method: 'POST', url: '/neo/tipos-vehiculo' });
    expect(req.request.body).toEqual({
      codigo: 'MENSAJERIA',
      nombre: 'Mensajería',
      modeloCobro: 'POR_MINUTO_AL_SALIR',
      tarifaPorMinuto: 0.35,
    });
    req.flush(
      { ...TIPOS[2], codigo: 'MENSAJERIA', nombre: 'Mensajería' },
      { status: 201, statusText: 'Created' },
    );
    await fixture.whenStable();

    listado().flush(TIPOS);
    await fixture.whenStable();
    const snackbar = await rootLoader.getHarness(MatSnackBarHarness);
    expect(await snackbar.getMessage()).toBe('Tipo MENSAJERIA creado');
    expect(await codigo.getValue()).toBe('');
  });

  it('con SIN_COBRO fija la tarifa en 0 y la deshabilita; con otro modelo la vuelve a habilitar', async () => {
    listado().flush(TIPOS);
    await fixture.whenStable();

    const tarifa = await loader.getHarness(
      MatInputHarness.with({ selector: '[formControlName="tarifaPorMinuto"]' }),
    );
    await tarifa.setValue('0.75');
    const modelo = await loader.getHarness(
      MatSelectHarness.with({ selector: '[formControlName="modeloCobro"]' }),
    );
    await modelo.open();
    await modelo.clickOptions({ text: 'Sin cobro' });
    await fixture.whenStable();
    expect(await tarifa.isDisabled()).toBeTrue();
    expect(await tarifa.getValue()).toBe('0');
    expect(fixture.componentInstance.form.getRawValue().tarifaPorMinuto).toBe(0);

    await modelo.open();
    await modelo.clickOptions({ text: 'Por minuto, acumulado al informe' });
    await fixture.whenStable();
    expect(await tarifa.isDisabled()).toBeFalse();
  });

  it('rechaza códigos que no cumplen el patrón y deshabilita el botón', async () => {
    listado().flush(TIPOS);
    await fixture.whenStable();
    const codigo = await loader.getHarness(
      MatInputHarness.with({ selector: '[formControlName="codigo"]' }),
    );
    await codigo.setValue('1abc');
    await codigo.blur();
    await fixture.whenStable();
    expect(elemento().querySelector('mat-error')?.textContent).toContain('Empieza por letra');
    const crear = await loader.getHarness(MatButtonHarness.with({ text: /Crear tipo/ }));
    expect(await crear.isDisabled()).toBeTrue();
  });

  it('deshabilita Eliminar para tipos integrados o en uso y explica el motivo', async () => {
    listado().flush(TIPOS);
    await fixture.whenStable();
    // `disabledInteractive` mantiene el botón enfocable (para el tooltip) y lo marca con aria-disabled.
    const deshabilitado = (aria: string) => boton(aria).getAttribute('aria-disabled') === 'true';
    expect(deshabilitado('Eliminar OFICIAL')).toBeTrue();
    expect(deshabilitado('Eliminar PROVEEDOR')).toBeTrue();
    expect(deshabilitado('Eliminar VISITA')).toBeFalse();
    expect(boton('Eliminar VISITA').disabled).toBeFalse();
    expect(fixture.componentInstance.motivoNoEliminable(TIPOS[1])).toContain('integrados');
    expect(fixture.componentInstance.motivoNoEliminable(TIPOS[2])).toContain('1 vehículo asignado');

    boton('Eliminar OFICIAL').click();
    await fixture.whenStable();
    expect(await rootLoader.getAllHarnesses(MatDialogHarness)).toEqual([]);
    httpTesting.expectNone((r) => r.method === 'DELETE');
  });

  it('elimina un tipo tras confirmar y recarga', async () => {
    listado().flush(TIPOS);
    await fixture.whenStable();
    boton('Eliminar VISITA').click();
    const dialogo = await rootLoader.getHarness(MatDialogHarness);
    expect(await dialogo.getTitleText()).toBe('¿Eliminar el tipo VISITA?');
    await (await dialogo.getHarness(MatButtonHarness.with({ text: 'Eliminar' }))).click();
    await fixture.whenStable();

    const req = httpTesting.expectOne({ method: 'DELETE', url: '/neo/tipos-vehiculo/VISITA' });
    req.flush(null, { status: 204, statusText: 'No Content' });
    await fixture.whenStable();
    listado().flush(TIPOS.slice(0, 3));
    await fixture.whenStable();
    expect(elemento().querySelectorAll('tr[mat-row]').length).toBe(3);
    const snackbar = await rootLoader.getHarness(MatSnackBarHarness);
    expect(await snackbar.getMessage()).toBe('Tipo VISITA eliminado');
  });

  it('no elimina si se cancela la confirmación', async () => {
    listado().flush(TIPOS);
    await fixture.whenStable();
    boton('Eliminar VISITA').click();
    const dialogo = await rootLoader.getHarness(MatDialogHarness);
    await (await dialogo.getHarness(MatButtonHarness.with({ text: 'Cancelar' }))).click();
    await fixture.whenStable();
    httpTesting.expectNone((r) => r.method === 'DELETE');
  });

  it('edita un tipo en el diálogo y envía el PUT', async () => {
    listado().flush(TIPOS);
    await fixture.whenStable();
    boton('Editar PROVEEDOR').click();
    const dialogo = await rootLoader.getHarness(MatDialogHarness);
    expect(await dialogo.getTitleText()).toContain('PROVEEDOR');

    const nombre = await dialogo.getHarness(
      MatInputHarness.with({ selector: '[formControlName="nombre"]' }),
    );
    expect(await nombre.getValue()).toBe('Proveedor');
    await nombre.setValue('Proveedor externo');
    const tarifa = await dialogo.getHarness(
      MatInputHarness.with({ selector: '[formControlName="tarifaPorMinuto"]' }),
    );
    await tarifa.setValue('0.3');
    const modelo = await dialogo.getHarness(MatSelectHarness);
    expect(await modelo.isDisabled()).toBeFalse();
    await (await dialogo.getHarness(MatButtonHarness.with({ text: 'Guardar' }))).click();
    await fixture.whenStable();

    const req = httpTesting.expectOne({ method: 'PUT', url: '/neo/tipos-vehiculo/PROVEEDOR' });
    expect(req.request.body).toEqual({
      nombre: 'Proveedor externo',
      modeloCobro: 'POR_MINUTO_AL_SALIR',
      tarifaPorMinuto: 0.3,
    });
    req.flush({ ...TIPOS[2], nombre: 'Proveedor externo', tarifaPorMinuto: 0.3 });
    await fixture.whenStable();
    listado().flush(TIPOS);
    await fixture.whenStable();
    const snackbar = await rootLoader.getHarness(MatSnackBarHarness);
    expect(await snackbar.getMessage()).toBe('Tipo PROVEEDOR actualizado');
  });

  it('en el diálogo de un tipo integrado el modelo está deshabilitado', async () => {
    listado().flush(TIPOS);
    await fixture.whenStable();
    boton('Editar OFICIAL').click();
    const dialogo = await rootLoader.getHarness(MatDialogHarness);
    const modelo = await dialogo.getHarness(MatSelectHarness);
    expect(await modelo.isDisabled()).toBeTrue();
    const tarifa = await dialogo.getHarness(
      MatInputHarness.with({ selector: '[formControlName="tarifaPorMinuto"]' }),
    );
    expect(await tarifa.isDisabled()).toBeTrue();
    await (await dialogo.getHarness(MatButtonHarness.with({ text: 'Cancelar' }))).click();
    await fixture.whenStable();
    httpTesting.expectNone((r) => r.method === 'PUT');
  });

  it('muestra el vacío, el error y reintenta', async () => {
    listado().flush([]);
    await fixture.whenStable();
    expect(elemento().textContent).toContain('No hay tipos de vehículo');

    fixture.componentInstance.cargar();
    listado().flush({ status: 500 }, { status: 500, statusText: 'Error' });
    await fixture.whenStable();
    expect(elemento().textContent).toContain('No se pudieron cargar los tipos');
    const reintentar = await loader.getHarness(MatButtonHarness.with({ text: 'Reintentar' }));
    await reintentar.click();
    listado().flush(TIPOS);
    await fixture.whenStable();
    expect(elemento().querySelectorAll('tr[mat-row]').length).toBe(4);
  });
});
