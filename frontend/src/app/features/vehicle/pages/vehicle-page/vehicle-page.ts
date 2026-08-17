import { Component, inject, signal, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';

import { VehicleService, VehicleListItem } from '../../services/vehicle-service';
import { VehicleListComponent } from '../../components/vehicle-list.component/vehicle-list.component';
import { VehicleDetailDialogComponent } from '../../components/vehicle-detail-dialog.component/vehicle-detail-dialog.component';

@Component({
  selector: 'app-vehicle-page',
  host: { class: 'block h-full' },
  imports: [VehicleListComponent],
  templateUrl: './vehicle-page.html',
})
export class VehiclePageComponent implements OnInit {
  private readonly vehicleService = inject(VehicleService);
  private readonly dialog = inject(MatDialog);

  protected readonly loading = signal(false);
  protected readonly vehicles = signal<VehicleListItem[]>([]);
  protected readonly total = signal(0);
  protected readonly pageIndex = signal(0);
  protected readonly pageSize = signal(10);

  ngOnInit(): void {
    this.loadVehicles();
  }

  protected loadVehicles(): void {
    this.loading.set(true);
    this.vehicleService.getVehicles(this.pageIndex(), this.pageSize()).subscribe({
      next: (res) => {
        this.vehicles.set(res.content);
        this.total.set(res.totalElements);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        console.error('Error al cargar vehículos', err);
      },
    });
  }

  protected onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadVehicles();
  }

  protected onDetail(vehicle: VehicleListItem): void {
    this.dialog.open(VehicleDetailDialogComponent, {
      data: { licensePlate: vehicle.licensePlate, showExit: false },
      width: '520px',
    });
  }
}