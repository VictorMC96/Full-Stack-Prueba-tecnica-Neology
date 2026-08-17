import { Component, output } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';

export interface VehicleFormValue {
  licensePlate: string;
  type: string;
  brand: string;
  model: string;
  color: string;
  bodyType: string;
  year: number | null;
  countryPlate: string;
}

@Component({
  selector: 'app-vehicle-form',
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
  ],
  templateUrl: './vehicle-form.component.html',
})
export class VehicleFormComponent {
  private readonly fb = new FormBuilder();

  // Emite el valor válido al padre (el modal) cuando se envía
  readonly submitForm = output<VehicleFormValue>();
  // Permite cancelar desde el form
  readonly cancel = output<void>();

  protected readonly vehicleTypes = [
    { value: 'OFFICIAL', label: 'Oficial' },
    { value: 'RESIDENT', label: 'Residente' },
    { value: 'NO_RESIDENT', label: 'No residente' },
  ];

  protected readonly bodyTypes = [
    'Sedán',
    'SUV',
    'Pickup',
    'Hatchback',
    'Van',
    'Coupé',
    'Otro',
  ];

  protected readonly form = this.fb.group({
    licensePlate: ['', [Validators.required, Validators.maxLength(10)]],
    type: ['NO_RESIDENT', [Validators.required]],
    brand: ['', [Validators.maxLength(50)]],
    model: ['', [Validators.maxLength(50)]],
    color: ['', [Validators.maxLength(30)]],
    bodyType: ['', [Validators.maxLength(30)]],
    year: [null as number | null],
    countryPlate: ['', [Validators.maxLength(40)]],
  });

  protected onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitForm.emit(this.form.getRawValue() as VehicleFormValue);
  }

  protected onCancel(): void {
    this.cancel.emit();
  }
}