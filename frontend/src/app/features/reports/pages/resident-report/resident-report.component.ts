import {
  AfterViewInit,
  Component,
  OnInit,
  ViewChild
} from '@angular/core';

import { FormControl } from '@angular/forms';

import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';

import { ParkingApiService } from '../../../../core/services/parking-api';
import { ResidentPayment } from '../../../../core/models/resident-payment.model';

@Component({
  selector: 'app-resident-report',
  standalone: false,
  templateUrl: './resident-report.component.html',
  styleUrl: './resident-report.component.scss'
})
export class ResidentReportComponent
  implements OnInit, AfterViewInit {

  displayedColumns: string[] = [
    'plate',
    'accumulatedMinutes',
    'amount'
  ];

  dataSource =
    new MatTableDataSource<ResidentPayment>();

  filterControl = new FormControl('', {
    nonNullable: true
  });

  loading = false;

  totalAmount = 0;

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort)
  sort!: MatSort;

  constructor(
    private readonly parkingApi: ParkingApiService,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.configureFilter();
    this.loadReport();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  loadReport(): void {
    this.loading = true;

    this.parkingApi.getResidentPayments().subscribe({
      next: report => {
        this.dataSource.data = report;

        this.totalAmount = report.reduce(
          (total, resident) =>
            total + resident.amount,
          0
        );

        this.loading = false;
      },

      error: error => {
        this.loading = false;

        this.snackBar.open(
          error.error?.message
            ?? 'No fue posible cargar el reporte',
          'Cerrar',
          {
            duration: 4000
          }
        );
      }
    });
  }

  private configureFilter(): void {
    this.dataSource.filterPredicate = (
      resident: ResidentPayment,
      filter: string
    ) => {
      return resident.plate
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
}