import {
  AfterViewInit,
  Component,
  OnInit,
  ViewChild
} from '@angular/core';
import { FormControl } from '@angular/forms';
import { Router } from '@angular/router';

import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';

import { ParkingApiService } from '../../../../core/services/parking-api';
import {
  Vehicle,
  VehicleType
} from '../../../../core/models/vehicle.model';

@Component({
  selector: 'app-vehicle-list',
  standalone: false,
  templateUrl: './vehicle-list.component.html',
  styleUrl: './vehicle-list.component.scss'
})
export class VehicleListComponent implements OnInit, AfterViewInit {

  displayedColumns: string[] = [
    'plate',
    'type',
    'actions'
  ];

  dataSource = new MatTableDataSource<Vehicle>();

  filterControl = new FormControl('', {
    nonNullable: true
  });

  loading = false;

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort)
  sort!: MatSort;

  constructor(
    private readonly parkingApi: ParkingApiService,
    private readonly router: Router,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.configureFilter();
    this.loadVehicles();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  loadVehicles(): void {
    this.loading = true;

    this.parkingApi.getVehicles().subscribe({
      next: vehicles => {
        this.dataSource.data = vehicles;
        this.loading = false;
      },
      error: error => {
        this.loading = false;

        this.snackBar.open(
          error.error?.message ?? 'Error al cargar los vehículos',
          'Cerrar',
          {
            duration: 4000
          }
        );
      }
    });
  }

  registerEntry(vehicle: Vehicle): void {
    this.parkingApi.registerEntry(vehicle.plate).subscribe({
      next: () => {
        this.snackBar.open(
          `Entrada registrada: ${vehicle.plate}`,
          'Cerrar',
          {
            duration: 3000
          }
        );

        this.loadVehicles();
      },
      error: error => {
        this.showError(error, 'No fue posible registrar la entrada');
      }
    });
  }

  registerExit(vehicle: Vehicle): void {
    this.parkingApi.registerExit(vehicle.plate).subscribe({
      next: stay => {
        const amount = stay.amount ?? 0;

        this.snackBar.open(
          `Salida registrada: ${vehicle.plate} - Cobro: $${amount.toFixed(2)}`,
          'Cerrar',
          {
            duration: 4000
          }
        );

        this.loadVehicles();
      },
      error: error => {
        this.showError(error, 'No fue posible registrar la salida');
      }
    });
  }

  viewDetail(vehicle: Vehicle): void {
    this.router.navigate([
      '/vehiculos',
      vehicle.plate
    ]);
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

  private configureFilter(): void {
    this.dataSource.filterPredicate = (
      vehicle: Vehicle,
      filter: string
    ) => {
      return vehicle.plate
        .toLowerCase()
        .includes(filter);
    };

    this.filterControl.valueChanges.subscribe(value => {
      this.dataSource.filter = value
        .trim()
        .toLowerCase();

      if (this.dataSource.paginator) {
        this.dataSource.paginator.firstPage();
      }
    });
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