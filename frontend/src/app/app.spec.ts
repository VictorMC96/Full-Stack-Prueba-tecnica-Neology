import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
  });

  it('renderiza el shell con el título y la navegación', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const elemento = fixture.nativeElement as HTMLElement;
    expect(elemento.querySelector('mat-toolbar')?.textContent).toContain(
      'Gestión de Acceso Vehicular',
    );
    expect(elemento.querySelectorAll('nav a').length).toBe(4);
    const enlaces = Array.from(elemento.querySelectorAll('nav a [matListItemTitle]')).map((a) =>
      a.textContent?.trim(),
    );
    expect(enlaces).toEqual(['Vehículos', 'Operación', 'Reporte', 'Tipos de vehículo']);
    expect(elemento.querySelector('main')).not.toBeNull();
  });
});
