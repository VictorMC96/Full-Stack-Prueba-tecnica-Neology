import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { StayService } from '../services/stay.service';
import { safePlateValidator, sanitizePlate } from '../shared/sanitize-input';

@Component({
  selector: 'app-vehicle-entry-form',
  imports: [ReactiveFormsModule],
  templateUrl: './vehicle-entry-form.html',
  styleUrl: './vehicle-entry-form.css'
})
export class VehicleEntryForm {
  private readonly stayService = inject(StayService);

  readonly form = new FormGroup({
    plate: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, safePlateValidator()]
    })
  });

  readonly submitting = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const plate = sanitizePlate(this.form.controls.plate.value);

    this.submitting.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.stayService.registerEntry({ plate }).subscribe({
      next: () => {
        this.submitting.set(false);
        this.successMessage.set(`Entry registered for plate ${plate}.`);
        this.form.reset();
      },
      error: () => {
        this.submitting.set(false);
        this.errorMessage.set('Could not register the entry. Please try again.');
      }
    });
  }
}