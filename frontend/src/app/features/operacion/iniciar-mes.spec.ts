import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MATERIAL_ANIMATIONS } from '@angular/material/core';
import { MatDialogHarness } from '@angular/material/dialog/testing';
import { MatSnackBarHarness } from '@angular/material/snack-bar/testing';
import { IniciarMes } from './iniciar-mes';

describe('IniciarMes', () => {
  let fixture: ComponentFixture<IniciarMes>;
  let httpTesting: HttpTestingController;
  let loader: HarnessLoader;
  let rootLoader: HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IniciarMes],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: MATERIAL_ANIMATIONS, useValue: { animationsDisabled: true } },
      ],
    }).compileComponents();
    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(IniciarMes);
    loader = TestbedHarnessEnvironment.loader(fixture);
    rootLoader = TestbedHarnessEnvironment.documentRootLoader(fixture);
    await fixture.whenStable();
  });

  afterEach(() => httpTesting.verify());

  async function abrirDialogo(): Promise<MatDialogHarness> {
    const boton = await loader.getHarness(MatButtonHarness.with({ text: /Iniciar nuevo mes/ }));
    await boton.click();
    const dialogo = await rootLoader.getHarness(MatDialogHarness);
    expect(await dialogo.getTitleText()).toBe('¿Iniciar un nuevo mes?');
    return dialogo;
  }

  it('no envía nada si se cancela', async () => {
    const dialogo = await abrirDialogo();
    const cancelar = await dialogo.getHarness(MatButtonHarness.with({ text: 'Cancelar' }));
    await cancelar.click();
    await fixture.whenStable();
    httpTesting.expectNone('/neo/mes/iniciar');
    expect(await rootLoader.getAllHarnesses(MatDialogHarness)).toEqual([]);
  });

  it('envía POST /neo/mes/iniciar al confirmar y avisa', async () => {
    const dialogo = await abrirDialogo();
    const confirmar = await dialogo.getHarness(MatButtonHarness.with({ text: 'Iniciar mes' }));
    await confirmar.click();
    await fixture.whenStable();

    const req = httpTesting.expectOne('/neo/mes/iniciar');
    expect(req.request.method).toBe('POST');
    req.flush({
      periodoAnterior: { id: 1, inicio: '2026-09-01T00:00:00Z', fin: '2026-09-30T12:00:00Z' },
      periodoActual: { id: 2, inicio: '2026-09-30T12:00:00Z', fin: null },
    });
    await fixture.whenStable();
    const snackbar = await rootLoader.getHarness(MatSnackBarHarness);
    expect(await snackbar.getMessage()).toBe('Nuevo mes iniciado');
  });
});
