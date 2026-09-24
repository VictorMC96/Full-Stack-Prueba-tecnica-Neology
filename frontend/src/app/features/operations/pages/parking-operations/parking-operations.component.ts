import { Component } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators
} from '@angular/forms';

import { MatSnackBar } from '@angular/material/snack-bar';

import { ParkingApiService } from '../../../../core/services/parking-api';
import { VehicleType } from '../../../../core/models/vehicle.model';

@Component({
  selector: 'app-parking-operations',
  standalone: false,
  templateUrl: './parking-operations.component.html',
  styleUrl: './parking-operations.component.scss'
})
export class ParkingOperationsComponent {

  vehicleTypes: {
    value: VehicleType;
    label: string;
  }[] = [
      {
        value: 'OFFICIAL',
        label: 'Oficial'
      },
      {
        value: 'RESIDENT',
        label: 'Residente'
      },
      {
        value: 'NON_RESIDENT',
        label: 'No residente'
      }
    ];

  registrationForm: FormGroup<{
    plate: FormControl<string>;
    type: FormControl<VehicleType>;
  }>;

  stayForm: FormGroup<{
    plate: FormControl<string>;
  }>;

  registeringVehicle = false;
  processingStay = false;
  startingMonth = false;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly parkingApi: ParkingApiService,
    private readonly snackBar: MatSnackBar
  ) {
    this.registrationForm = this.formBuilder.nonNullable.group({
      plate: [
        '',
        [
          Validators.required,
          Validators.maxLength(20)
        ]
      ],
      type: [
        'RESIDENT' as VehicleType,
        Validators.required
      ]
    });

    this.stayForm = this.formBuilder.nonNullable.group({
      plate: [
        '',
        [
          Validators.required,
          Validators.maxLength(20)
        ]
      ]
    });
  }

  registerVehicle(): void {

    if (this.registrationForm.invalid) {
      this.registrationForm.markAllAsTouched();
      return;
    }

    const plate = this.normalizePlate(
      this.registrationForm.controls.plate.value
    );

    const type =
      this.registrationForm.controls.type.value;

    this.registeringVehicle = true;

    const request$ = type === 'OFFICIAL'
      ? this.parkingApi.registerOfficial(plate)
      : type === 'RESIDENT'
        ? this.parkingApi.registerResident(plate)
        : this.parkingApi.registerNonResident(plate);

    request$.subscribe({
      next: vehicle => {
        this.registeringVehicle = false;

        this.snackBar.open(
          `Vehículo ${vehicle.plate} registrado correctamente`,
          'Cerrar',
          {
            duration: 3000
          }
        );

        this.registrationForm.reset({
          plate: '',
          type: 'RESIDENT'
        });
      },

      error: error => {
        this.registeringVehicle = false;

        this.showError(
          error,
          'No fue posible registrar el vehículo'
        );
      }
    });
  }

  registerEntry(): void {

    if (this.stayForm.invalid) {
      this.stayForm.markAllAsTouched();
      return;
    }

    const plate = this.normalizePlate(
      this.stayForm.controls.plate.value
    );

    this.processingStay = true;

    this.parkingApi.registerEntry(plate).subscribe({
      next: stay => {
        this.processingStay = false;

        this.snackBar.open(
          `Entrada registrada: ${stay.plate}`,
          'Cerrar',
          {
            duration: 3000
          }
        );

        this.stayForm.reset();
      },

      error: error => {
        this.processingStay = false;

        this.showError(
          error,
          'No fue posible registrar la entrada'
        );
      }
    });
  }

  registerExit(): void {

    if (this.stayForm.invalid) {
      this.stayForm.markAllAsTouched();
      return;
    }

    const plate = this.normalizePlate(
      this.stayForm.controls.plate.value
    );

    this.processingStay = true;

    this.parkingApi.registerExit(plate).subscribe({
      next: stay => {
        this.processingStay = false;

        const amount = stay.amount ?? 0;

        this.snackBar.open(
          `Salida registrada: ${stay.plate} - Cobro: $${amount.toFixed(2)}`,
          'Cerrar',
          {
            duration: 4000
          }
        );

        this.stayForm.reset();
      },

      error: error => {
        this.processingStay = false;

        this.showError(
          error,
          'No fue posible registrar la salida'
        );
      }
    });
  }

  startNewMonth(): void {

    const confirmed = window.confirm(
      '¿Deseas iniciar un nuevo mes? Se eliminarán las estancias y se reiniciarán los minutos acumulados de los residentes.'
    );

    if (!confirmed) {
      return;
    }

    this.startingMonth = true;

    this.parkingApi.startNewMonth().subscribe({
      next: response => {
        this.startingMonth = false;

        this.snackBar.open(
          response.message,
          'Cerrar',
          {
            duration: 4000
          }
        );
      },

      error: error => {
        this.startingMonth = false;

        this.showError(
          error,
          'No fue posible iniciar el nuevo mes'
        );
      }
    });
  }

  private normalizePlate(plate: string): string {
    return plate
      .trim()
      .toUpperCase();
  }

  private showError(
    error: any,
    defaultMessage: string
  ): void {

    this.snackBar.open(
      error.error?.message ?? defaultMessage,
      'Cerrar',
      {
        duration: 4000
      }
    );
  }
}