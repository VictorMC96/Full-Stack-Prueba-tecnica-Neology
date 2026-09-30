import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router, provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { routes } from './app.routes';

describe('routes', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(routes, withComponentInputBinding()),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
  });

  it('redirige la raíz a /vehiculos', async () => {
    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/');
    expect(TestBed.inject(Router).url).toBe('/vehiculos');
  });

  it('carga un componente con un solo h1 en cada ruta del manifiesto', async () => {
    const harness = await RouterTestingHarness.create();
    for (const url of [
      '/vehiculos',
      '/vehiculos/ABC123',
      '/operacion',
      '/reporte',
      '/tipos-vehiculo',
      '/no-existe',
    ]) {
      await harness.navigateByUrl(url);
      await harness.fixture.whenStable();
      const h1 = harness.routeNativeElement?.querySelectorAll('h1') ?? [];
      expect(h1.length).withContext(url).toBe(1);
    }
  });
});
