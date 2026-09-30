import { TestBed } from '@angular/core/testing';
import { EstadoChip } from './estado-chip';

describe('EstadoChip', () => {
  const casos: [string, string, string][] = [
    ['OFICIAL', 'Oficial', 'chip--oficial'],
    ['RESIDENTE', 'Residente', 'chip--residente'],
    ['NO_RESIDENTE', 'No residente', 'chip--no-residente'],
    ['DENTRO', 'Dentro', 'chip--dentro'],
    ['FUERA', 'Fuera', 'chip--fuera'],
  ];

  for (const [estado, texto, clase] of casos) {
    it(`muestra ${texto} para ${estado}`, async () => {
      const fixture = TestBed.createComponent(EstadoChip);
      fixture.componentRef.setInput('estado', estado);
      await fixture.whenStable();
      const chip = (fixture.nativeElement as HTMLElement).querySelector('.chip');
      expect(chip?.textContent?.trim()).toBe(texto);
      expect(chip?.classList.contains(clase)).toBeTrue();
    });
  }

  it('muestra el nombre del tipo cuando llega y conserva el color del código conocido', async () => {
    const fixture = TestBed.createComponent(EstadoChip);
    fixture.componentRef.setInput('estado', 'RESIDENTE');
    fixture.componentRef.setInput('nombre', 'Residente del edificio');
    await fixture.whenStable();
    const chip = (fixture.nativeElement as HTMLElement).querySelector('.chip');
    expect(chip?.textContent?.trim()).toBe('Residente del edificio');
    expect(chip?.classList.contains('chip--residente')).toBeTrue();
  });

  it('usa color neutro y el nombre (o el código) para tipos desconocidos', async () => {
    const fixture = TestBed.createComponent(EstadoChip);
    fixture.componentRef.setInput('estado', 'PROVEEDOR');
    fixture.componentRef.setInput('nombre', 'Proveedor');
    await fixture.whenStable();
    const chip = (fixture.nativeElement as HTMLElement).querySelector('.chip');
    expect(chip?.textContent?.trim()).toBe('Proveedor');
    expect(chip?.classList.contains('chip--neutro')).toBeTrue();

    fixture.componentRef.setInput('nombre', undefined);
    await fixture.whenStable();
    expect(chip?.textContent?.trim()).toBe('PROVEEDOR');
  });
});
