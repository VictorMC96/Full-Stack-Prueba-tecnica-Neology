import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const PLACA_REGEX = /^[A-Z0-9]{3,10}$/;

/** Misma regla que el backend: mayúsculas, sin espacios ni guiones. */
export function normalizarPlaca(valor: string | null | undefined): string {
  return (valor ?? '').toUpperCase().replace(/[\s-]/g, '');
}

export const placaValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const valor = control.value;
  if (valor === null || valor === undefined || valor === '') {
    return null;
  }
  return PLACA_REGEX.test(normalizarPlaca(String(valor))) ? null : { placa: true };
};
