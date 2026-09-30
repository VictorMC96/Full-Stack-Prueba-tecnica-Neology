import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatSelectHarness } from '@angular/material/select/testing';
import { MatSnackBarHarness } from '@angular/material/snack-bar/testing';
import { AltaForm } from './alta-form';

describe('AltaForm', () => {
  let fixture: ComponentFixture<AltaForm>;
  let httpTesting: HttpTestingController;
  let loader: HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AltaForm],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(AltaForm);
    loader = TestbedHarnessEnvironment.loader(fixture);
    await fixture.whenStable();
  });

  afterEach(() => httpTesting.verify());

  it('envía a /oficiales por defecto y muestra el snackbar', async () => {
    const input = await loader.getHarness(MatInputHarness);
    await input.setValue('ofi-002');
    const boton = await loader.getHarness(MatButtonHarness.with({ text: 'Dar de alta' }));
    await boton.click();

    const req = httpTesting.expectOne('/neo/vehiculos/oficiales');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ placa: 'OFI002' });
    req.flush(
      { id: 3, placa: 'OFI002', tipo: 'OFICIAL', dentro: false, createdAt: '2026-09-30T10:00:00Z' },
      { status: 201, statusText: 'Created' },
    );
    await fixture.whenStable();

    const snackbar =
      await TestbedHarnessEnvironment.documentRootLoader(fixture).getHarness(MatSnackBarHarness);
    expect(await snackbar.getMessage()).toBe('Vehículo OFI002 registrado como Oficial');
    expect(await input.getValue()).toBe('');
  });

  it('envía a /residentes cuando se elige Residente', async () => {
    const select = await loader.getHarness(MatSelectHarness);
    await select.open();
    await select.clickOptions({ text: 'Residente' });
    const input = await loader.getHarness(MatInputHarness);
    await input.setValue('RES009');
    const boton = await loader.getHarness(MatButtonHarness.with({ text: 'Dar de alta' }));
    await boton.click();

    const req = httpTesting.expectOne('/neo/vehiculos/residentes');
    expect(req.request.body).toEqual({ placa: 'RES009' });
    req.flush({
      id: 4,
      placa: 'RES009',
      tipo: 'RESIDENTE',
      dentro: false,
      createdAt: '2026-09-30T10:00:00Z',
    });
    await fixture.whenStable();
  });
});
