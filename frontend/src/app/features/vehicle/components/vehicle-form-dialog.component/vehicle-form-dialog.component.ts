import { Component, inject, signal } from '@angular/core';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialogModule,
} from '@angular/material/dialog';
import { VehicleFormComponent, VehicleFormValue } from '../vehicle-form.component/vehicle-form.component';
import { VehicleService } from '../../services/vehicle-service';
import { ParkingSpot } from '../../../parking-lot/components/parking-lot-spot/parking-lot-spot';

export interface VehicleFormDialogData {
  spot: ParkingSpot;
}

@Component({
  selector: 'app-vehicle-form-dialog',
  imports: [MatDialogModule, VehicleFormComponent],
  templateUrl: './vehicle-form-dialog.component.html',
})
export class VehicleFormDialogComponent {
  private readonly dialogRef = inject(MatDialogRef<VehicleFormDialogComponent>);
  private readonly vehicleService = inject(VehicleService);
  protected readonly data = inject<VehicleFormDialogData>(MAT_DIALOG_DATA);

  protected readonly saving = signal(false);
  protected readonly errorMsg = signal<string | null>(null);

  protected onSubmit(value: VehicleFormValue): void {
    this.saving.set(true);
    this.errorMsg.set(null);

    this.vehicleService.createVehicle(value).subscribe({
      next: () => {
        this.saving.set(false);
        this.dialogRef.close({ registered: true, spot: this.data.spot, vehicle: value });
      },
      error: (err) => {
        this.saving.set(false);
        this.errorMsg.set(
          err?.status === 409
            ? 'Ya existe un vehículo con esa placa.'
            : 'No se pudo registrar el vehículo.'
        );
        console.error('Error al registrar vehículo', err);
      },
    });
  }

  protected onCancel(): void {
    this.dialogRef.close();
  }
}