import { FormControl } from '@angular/forms';
import { normalizarPlaca, placaValidator } from './placa.validator';

describe('placaValidator', () => {
  it('normaliza a mayúsculas sin espacios ni guiones', () => {
    expect(normalizarPlaca(' abc-123 d ')).toBe('ABC123D');
    expect(normalizarPlaca(null)).toBe('');
  });

  it('acepta una placa válida tras normalizar', () => {
    expect(placaValidator(new FormControl('abc-123 d'))).toBeNull();
  });

  it('rechaza placas cortas o con símbolos con la clave placa', () => {
    expect(placaValidator(new FormControl('AB'))).toEqual({ placa: true });
    expect(placaValidator(new FormControl('ABC<1>'))).toEqual({ placa: true });
    expect(placaValidator(new FormControl('ABCDEFGHIJK'))).toEqual({ placa: true });
  });

  it('deja el vacío a Validators.required', () => {
    expect(placaValidator(new FormControl(''))).toBeNull();
  });
});
