import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

const SAFE_PLATE_PATTERN = /^[A-Za-z0-9-]+$/;

export function safePlateValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = (control.value ?? '').toString().trim();
    if (!value) {
      return null; // Validators.required already covers the empty case
    }
    return SAFE_PLATE_PATTERN.test(value) ? null : { unsafePlate: true };
  };
}

export function sanitizePlate(value: string): string {
  return (value ?? '').replace(/[^A-Za-z0-9-]/g, '').toUpperCase();
}