import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatInputHarness } from '@angular/material/input/testing';
import { EstanciaResponse, MomentoCobro } from '../../core/modelos/modelos';
import { SalidaForm } from './salida-form';

function estancia(momentoCobro: MomentoCobro, importe: number): EstanciaResponse {
  return {
    id: 7,
    placa: 'NR0001',
    tipo: 'NO_RESIDENTE',
    entrada: '2026-09-30T10:00:00Z',
    salida: '2026-09-30T11:30:00Z',
    minutos: 90,
    importe,
    momentoCobro,
    periodoId: null,
  };
}

describe('SalidaForm', () => {
  let fixture: ComponentFixture<SalidaForm>;
  let httpTesting: HttpTestingController;
  let loader: HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SalidaForm],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(SalidaForm);
    loader = TestbedHarnessEnvironment.loader(fixture);
    await fixture.whenStable();
  });

  afterEach(() => httpTesting.verify());

  async function registrarSalida(respuesta: EstanciaResponse): Promise<HTMLElement> {
    const input = await loader.getHarness(MatInputHarness);
    await input.setValue('nr 0001');
    const boton = await loader.getHarness(MatButtonHarness.with({ text: 'Registrar salida' }));
    await boton.click();
    const req = httpTesting.expectOne('/neo/estancias/salida');
    expect(req.request.body).toEqual({ placa: 'NR0001' });
    req.flush(respuesta);
    await fixture.whenStable();
    return (fixture.nativeElement as HTMLElement).querySelector('.resultado') as HTMLElement;
  }

  it('muestra minutos, importe y "Cobrar al salir"', async () => {
    const resultado = await registrarSalida(estancia('AL_SALIR', 45));
    expect(resultado.textContent).toContain('90');
    expect(resultado.textContent).toContain('$45.00');
    expect(resultado.textContent).toContain('Cobrar al salir');
  });

  it('etiqueta el acumulado mensual y el sin cobro', async () => {
    let resultado = await registrarSalida(estancia('INFORME_MENSUAL', 4.5));
    expect(resultado.textContent).toContain('Se acumula al informe mensual');
    resultado = await registrarSalida(estancia('NINGUNO', 0));
    expect(resultado.textContent).toContain('Sin cobro');
  });

  it('deshabilita el botón con placa vacía', async () => {
    const boton = await loader.getHarness(MatButtonHarness.with({ text: 'Registrar salida' }));
    expect(await boton.isDisabled()).toBeTrue();
  });
});
