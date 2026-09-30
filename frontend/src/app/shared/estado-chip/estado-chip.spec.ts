import { TestBed } from '@angular/core/testing';
import { EstadoChip, EstadoChipValor } from './estado-chip';

describe('EstadoChip', () => {
  const casos: [EstadoChipValor, string, string][] = [
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
});
