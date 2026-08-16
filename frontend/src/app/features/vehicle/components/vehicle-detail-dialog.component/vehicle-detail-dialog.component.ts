import { Component, computed, inject, signal } from '@angular/core';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialogModule,
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CurrencyPipe, DatePipe } from '@angular/common';

import { VehicleService, VehicleStayDetail } from '../../services/vehicle-service';
import { PaymentService } from '../../../payment/services/payment-service';
import { calculateFare, elapsedMinutes } from '../../../parking-lot/services/pricing-fare';

export interface VehicleDetailDialogData {
  licensePlate: string;
  showExit: boolean;
}

@Component({
  selector: 'app-vehicle-detail-dialog',
  imports: [MatDialogModule, MatButtonModule, CurrencyPipe, DatePipe],
  templateUrl: './vehicle-detail-dialog.component.html',
})
export class VehicleDetailDialogComponent {
  private readonly dialogRef = inject(MatDialogRef<VehicleDetailDialogComponent>);
  private readonly vehicleService = inject(VehicleService);
  private readonly paymentService = inject(PaymentService);
  protected readonly data = inject<VehicleDetailDialogData>(MAT_DIALOG_DATA);

  protected readonly loading = signal(true);
  protected readonly finishing = signal(false);
  protected readonly errorMsg = signal<string | null>(null);
  protected readonly detail = signal<VehicleStayDetail | null>(null);

  protected readonly minutes = computed(() => {
    const d = this.detail();
    return d ? elapsedMinutes(d.entryDate) : 0;
  });

  protected readonly total = computed(() => {
    const d = this.detail();
    return d ? calculateFare(d.type, d.entryDate) : 0;
  });

  constructor() {
    this.vehicleService.getDetail(this.data.licensePlate).subscribe({
      next: (d) => {
        this.detail.set(d);
        this.loading.set(false);
      },
      error: (err) => {
        this.errorMsg.set('No se pudo cargar el detalle del vehículo.');
        this.loading.set(false);
        console.error(err);
      },
    });
  }

  protected onFinish(): void {
    const d = this.detail();
    if (!d) return;

    this.finishing.set(true);
    this.paymentService
      .createPayment({ stayId: d.stayId, amount: this.total(), status: 'PAID' })
      .subscribe({
        next: () => {
          this.finishing.set(false);
          this.dialogRef.close({ finished: true });
        },
        error: (err) => {
          this.finishing.set(false);
          this.errorMsg.set('No se pudo registrar el pago.');
          console.error(err);
        },
      });
  }

  protected onClose(): void {
    this.dialogRef.close();
  }

  protected showExitInfo(d: VehicleStayDetail): boolean {
    return this.data.showExit && !!d.entryDate;
  }
}