import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { VehicleRequest, VehicleType } from '../models/vehicle.model';
import { VehicleService } from '../services/vehicle.service';
import { safePlateValidator, sanitizePlate } from '../shared/sanitize-input';

@Component({
  selector: 'app-vehicle-register-form',
  imports: [ReactiveFormsModule],
  templateUrl: './vehicle-register-form.html',
  styleUrl: './vehicle-register-form.css'
})
export class VehicleRegisterForm {
  private readonly vehicleService = inject(VehicleService);
  private readonly router = inject(Router);

  readonly form = new FormGroup({
    plate: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, safePlateValidator()]
    }),
    type: new FormControl<VehicleType>('RESIDENT', {
      nonNullable: true,
      validators: [Validators.required]
    })
  });

  readonly submitting = signal(false);
  readonly errorMessage = signal<string | null>(null);

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const body: VehicleRequest = { plate: sanitizePlate(this.form.controls.plate.value) };
    const type = this.form.controls.type.value;

    this.submitting.set(true);
    this.errorMessage.set(null);

    this.pickRequest(type, body).subscribe({
      next: () => {
        this.submitting.set(false);
        this.router.navigate(['/vehicles']);
      },
      error: () => {
        this.submitting.set(false);
        this.errorMessage.set('Could not register the vehicle. Please try again.');
      }
    });
  }

  private pickRequest(type: VehicleType, body: VehicleRequest): Observable<void> {
    switch (type) {
      case 'RESIDENT': return this.vehicleService.registerResident(body);
      case 'NO_RESIDENT': return this.vehicleService.registerNotResident(body);
      case 'OFFICIALS': return this.vehicleService.registerOfficial(body);
    }
  }
}