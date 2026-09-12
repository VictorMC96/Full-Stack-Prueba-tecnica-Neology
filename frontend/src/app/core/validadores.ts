import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/** Mismo rango que aplica el backend al normalizar la placa. */
export const LARGO_MINIMO_PLACA = 5;
export const LARGO_MAXIMO_PLACA = 10;

const SOLO_ALFANUMERICOS = /^[A-Z0-9]+$/;

/** Quita espacios y guiones y pasa a mayusculas, igual que el backend. */
export function limpiarPlaca(placa: string): string {
  return (placa ?? '').trim().toUpperCase().replace(/[\s-]/g, '');
}

/**
 * Valida la placa contra las mismas reglas del backend: solo letras y numeros,
 * y entre 5 y 10 caracteres una vez quitados los espacios y los guiones.
 */
export function validadorPlaca(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const valor = control.value as string;
    if (!valor) {
      return null;
    }
    const limpia = limpiarPlaca(valor);
    if (!SOLO_ALFANUMERICOS.test(limpia)) {
      return { placaCaracteres: true };
    }
    if (limpia.length < LARGO_MINIMO_PLACA || limpia.length > LARGO_MAXIMO_PLACA) {
      return { placaLargo: { minimo: LARGO_MINIMO_PLACA, maximo: LARGO_MAXIMO_PLACA } };
    }
    return null;
  };
}
