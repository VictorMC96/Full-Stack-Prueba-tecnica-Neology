import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { ExitResponse } from '../models/stay.model';
import { StayService } from '../services/stay.service';
import { safePlateValidator, sanitizePlate } from '../shared/sanitize-input';

@Component({
  selector: 'app-vehicle-exit-form',
  imports: [ReactiveFormsModule, DecimalPipe],
  templateUrl: './vehicle-exit-form.html',
  styleUrl: './vehicle-exit-form.css'
})
export class VehicleExitForm {
  private readonly stayService = inject(StayService);

  readonly form = new FormGroup({
    plate: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, safePlateValidator()]
    })
  });

  readonly submitting = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly result = signal<ExitResponse | null>(null);

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const plate = sanitizePlate(this.form.controls.plate.value);

    this.submitting.set(true);
    this.errorMessage.set(null);
    this.result.set(null);

    this.stayService.registerExit({ plate }).subscribe({
      next: (response) => {
        this.submitting.set(false);
        this.result.set(response);
        this.form.reset();
      },
      error: () => {
        this.submitting.set(false);
        this.errorMessage.set('Could not register the exit. Please try again.');
      }
    });
  }
}