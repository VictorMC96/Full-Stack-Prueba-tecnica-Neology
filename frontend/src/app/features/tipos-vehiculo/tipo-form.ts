import { DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AbstractControl, FormControl, ValidationErrors, Validators } from '@angular/forms';
import { ModeloCobro } from '../../core/modelos/modelos';

export const CODIGO_TIPO_REGEX = /^[A-Z][A-Z0-9_]{2,19}$/;

export function codigoTipoValidator(control: AbstractControl): ValidationErrors | null {
  const valor = control.value;
  if (valor === null || valor === undefined || valor === '') {
    return null;
  }
  return CODIGO_TIPO_REGEX.test(String(valor).toUpperCase().trim()) ? null : { codigo: true };
}

export interface CamposTipo {
  nombre: FormControl<string>;
  modeloCobro: FormControl<ModeloCobro>;
  tarifaPorMinuto: FormControl<number>;
}

/** Controles comunes al alta y a la edición de un tipo de vehículo. */
export function camposTipo(inicial?: {
  nombre?: string;
  modeloCobro?: ModeloCobro;
  tarifaPorMinuto?: number;
}): CamposTipo {
  return {
    nombre: new FormControl(inicial?.nombre ?? '', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(2), Validators.maxLength(40)],
    }),
    modeloCobro: new FormControl<ModeloCobro>(inicial?.modeloCobro ?? 'POR_MINUTO_AL_SALIR', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    tarifaPorMinuto: new FormControl(inicial?.tarifaPorMinuto ?? 0, {
      nonNullable: true,
      validators: [Validators.required, Validators.min(0)],
    }),
  };
}

/** Con `SIN_COBRO` la tarifa se fija en 0 y se deshabilita; con los demás modelos se habilita. */
export function enlazarTarifaAlModelo(form: {
  controls: Pick<CamposTipo, 'modeloCobro' | 'tarifaPorMinuto'>;
}): void {
  const destroyRef = inject(DestroyRef);
  const aplicar = (modelo: ModeloCobro) => {
    const tarifa = form.controls.tarifaPorMinuto;
    if (modelo === 'SIN_COBRO') {
      tarifa.setValue(0);
      tarifa.disable();
    } else if (tarifa.disabled) {
      tarifa.enable();
    }
  };
  aplicar(form.controls.modeloCobro.value);
  form.controls.modeloCobro.valueChanges
    .pipe(takeUntilDestroyed(destroyRef))
    .subscribe((modelo) => aplicar(modelo));
}
