import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Operacion } from './operacion';

describe('Operacion', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Operacion],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
  });

  it('compone las tres tarjetas bajo un único h1', async () => {
    const fixture = TestBed.createComponent(Operacion);
    await fixture.whenStable();
    const elemento = fixture.nativeElement as HTMLElement;
    expect(elemento.querySelectorAll('h1').length).toBe(1);
    expect(elemento.querySelector('h1')?.textContent).toContain('Operación');
    const titulos = Array.from(elemento.querySelectorAll('h2')).map((h) => h.textContent?.trim());
    expect(titulos).toEqual(['Registrar movimiento', 'Alta de vehículo', 'Iniciar nuevo mes']);
    expect(elemento.querySelectorAll('mat-label').length).toBe(3);
    expect(elemento.querySelector('.rejilla > :first-child')?.tagName.toLowerCase()).toBe(
      'app-movimiento-form',
    );
  });
});
