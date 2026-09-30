import { DuracionPipe } from './duracion.pipe';

describe('DuracionPipe', () => {
  const pipe = new DuracionPipe();

  it('convierte segundos a mm:ss sin tope de minutos', () => {
    expect(pipe.transform(605)).toBe('10:05');
    expect(pipe.transform(5)).toBe('0:05');
    expect(pipe.transform(12600)).toBe('210:00');
    expect(pipe.transform(0)).toBe('0:00');
  });

  it('descarta fracciones de segundo', () => {
    expect(pipe.transform(59.9)).toBe('0:59');
  });

  it('muestra un guion sin valor', () => {
    expect(pipe.transform(null)).toBe('—');
    expect(pipe.transform(undefined)).toBe('—');
  });
});
