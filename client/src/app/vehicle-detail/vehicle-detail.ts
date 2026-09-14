import { Component, effect, inject, input, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Vehicle } from '../models/vehicle.model';
import { Stay } from '../models/stay.model';
import { ResidentPaymentResponse } from '../models/resident-payment.model';
import { VehicleService } from '../services/vehicle.service';
import { StayService } from '../services/stay.service';
import { ResidentService } from '../services/resident.service';
import { vehicleTypeLabel } from '../shared/vehicle-type-label';

const STAY_PAGE_SIZE = 5;

@Component({
  selector: 'app-vehicle-detail',
  imports: [RouterLink, DatePipe, DecimalPipe],
  templateUrl: './vehicle-detail.html',
  styleUrl: './vehicle-detail.css'
})
export class VehicleDetail {
  private readonly vehicleService = inject(VehicleService);
  private readonly stayService = inject(StayService);
  private readonly residentService = inject(ResidentService);

  protected readonly vehicleTypeLabel = vehicleTypeLabel;

  // Bound automatically from the ":plate" route parameter, thanks to
  // withComponentInputBinding() in app.config.ts.
  readonly plate = input.required<string>();

  readonly vehicle = signal<Vehicle | null>(null);
  readonly vehicleLoading = signal(false);
  readonly vehicleError = signal<string | null>(null);

  readonly stays = signal<Stay[]>([]);
  readonly staysLoading = signal(false);
  readonly stayPage = signal(0);
  readonly stayTotalPages = signal(0);

  readonly payment = signal<ResidentPaymentResponse | null>(null);
  readonly paymentLoading = signal(false);

  constructor() {
    // Runs on init and again every time "plate" changes - for example
    // if the user navigates straight from one vehicle's detail page
    // to another, reusing the same component instance.
    effect(() => {
      const plate = this.plate();
      this.loadVehicle(plate);
      this.loadStays(plate, 0);
    });
  }

  private loadVehicle(plate: string): void {
    this.vehicleLoading.set(true);
    this.vehicleError.set(null);
    this.payment.set(null);

    // There is no "get one vehicle" endpoint, so we search by plate
    // and pick the exact match out of the results.
    this.vehicleService.getVehicles(plate, 0, 50).subscribe({
      next: (response) => {
        const found = response.content.find((item) => item.plate === plate) ?? null;
        this.vehicle.set(found);
        this.vehicleLoading.set(false);

        if (found?.type === 'RESIDENT') {
          this.loadPayment(found.plate);
        }
      },
      error: () => {
        this.vehicleError.set('Could not load this vehicle.');
        this.vehicleLoading.set(false);
      }
    });
  }

  private loadPayment(plate: string): void {
    this.paymentLoading.set(true);

    // There is no endpoint for a single resident's payment, so we
    // load the full list and find the entry that matches this plate.
    this.residentService.getPayments().subscribe({
      next: (payments) => {
        this.payment.set(payments.find((item) => item.plate === plate) ?? null);
        this.paymentLoading.set(false);
      },
      error: () => {
        this.paymentLoading.set(false);
      }
    });
  }

  loadStays(plate: string, page: number): void {
    this.staysLoading.set(true);

    this.stayService.getStays(plate, page, STAY_PAGE_SIZE).subscribe({
      next: (response) => {
        this.stays.set(response.content);
        this.stayTotalPages.set(response.totalPages);
        this.stayPage.set(page);
        this.staysLoading.set(false);
      },
      error: () => {
        this.staysLoading.set(false);
      }
    });
  }

  goToStayPage(newPage: number): void {
    if (newPage < 0 || newPage >= this.stayTotalPages()) {
      return;
    }
    this.loadStays(this.plate(), newPage);
  }
}