import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

//Rango-Placa
export const LARGO_MINIMO_PLACA = 5;
export const LARGO_MAXIMO_PLACA = 10;

const SOLO_ALFANUMERICOS = /^[A-Z0-9]+$/;

//Limpieza-Texto
export function limpiarPlaca(placa: string): string {
  return (placa ?? '').trim().toUpperCase().replace(/[\s-]/g, '');
}

//Validacion-Placa
export function validadorPlaca(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const valor = control.value as string;
    if (!valor) {
      return null;
    }
    const limpia = limpiarPlaca(valor);
    //Revision-Caracteres
    if (!SOLO_ALFANUMERICOS.test(limpia)) {
      return { placaCaracteres: true };
    }
    //Revision-Largo
    if (limpia.length < LARGO_MINIMO_PLACA || limpia.length > LARGO_MAXIMO_PLACA) {
      return { placaLargo: { minimo: LARGO_MINIMO_PLACA, maximo: LARGO_MAXIMO_PLACA } };
    }
    return null;
  };
}
