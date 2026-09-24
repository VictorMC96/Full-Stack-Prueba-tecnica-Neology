import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';

import { MatSnackBar } from '@angular/material/snack-bar';

import { ParkingApiService } from '../../../../core/services/parking-api';
import { Vehicle, VehicleType } from '../../../../core/models/vehicle.model';
import { Stay } from '../../../../core/models/stay.model';
import { ResidentPayment } from '../../../../core/models/resident-payment.model';

@Component({
  selector: 'app-vehicle-detail',
  standalone: false,
  templateUrl: './vehicle-detail.component.html',
  styleUrl: './vehicle-detail.component.scss'
})
export class VehicleDetailComponent implements OnInit {

  vehicle?: Vehicle;

  stays: Stay[] = [];

  residentPayment?: ResidentPayment;

  loading = false;

  displayedColumns: string[] = [
    'entryDateTime',
    'exitDateTime',
    'durationMinutes',
    'amount'
  ];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly parkingApi: ParkingApiService,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const plate = this.route.snapshot.paramMap.get('plate');

    if (!plate) {
      this.router.navigate(['/vehiculos']);
      return;
    }

    this.loadVehicleDetail(plate);
  }

  goBack(): void {
    this.router.navigate(['/vehiculos']);
  }

  getVehicleTypeLabel(type: VehicleType): string {
    switch (type) {
      case 'OFFICIAL':
        return 'Oficial';

      case 'RESIDENT':
        return 'Residente';

      case 'NON_RESIDENT':
        return 'No residente';
    }
  }

  private loadVehicleDetail(plate: string): void {
    this.loading = true;

    forkJoin({
      vehicle: this.parkingApi.getVehicle(plate),
      stays: this.parkingApi.getVehicleStays(plate),
      residentPayments: this.parkingApi.getResidentPayments()
    }).subscribe({
      next: result => {
        this.vehicle = result.vehicle;
        this.stays = result.stays;

        if (result.vehicle.type === 'RESIDENT') {
          this.residentPayment =
            result.residentPayments.find(
              payment => payment.plate === result.vehicle.plate
            );
        }

        this.loading = false;
      },

      error: error => {
        this.loading = false;

        this.snackBar.open(
          error.error?.message ?? 'No fue posible cargar el vehículo',
          'Cerrar',
          {
            duration: 4000
          }
        );
      }
    });
  }
}