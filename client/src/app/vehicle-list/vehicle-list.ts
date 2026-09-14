import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { Vehicle } from '../models/vehicle.model';
import { VehicleService } from '../services/vehicle.service';
import { sanitizePlate } from '../shared/sanitize-input';
import { vehicleTypeLabel } from '../shared/vehicle-type-label';

type SortColumn = 'plate' | 'type';
type SortDirection = 'asc' | 'desc';

@Component({
  selector: 'app-vehicle-list',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './vehicle-list.html',
  styleUrl: './vehicle-list.css'
})
export class VehicleList implements OnInit {
  private readonly vehicleService = inject(VehicleService);

  protected readonly vehicleTypeLabel = vehicleTypeLabel;

  readonly plateFilterControl = new FormControl('', { nonNullable: true });

  readonly vehicles = signal<Vehicle[]>([]);
  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly page = signal(0);
  readonly size = signal(10);
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);

  readonly sortColumn = signal<SortColumn | null>(null);
  readonly sortDirection = signal<SortDirection>('asc');

  readonly sortedVehicles = computed(() => {
    const items = [...this.vehicles()];
    const column = this.sortColumn();
    if (!column) {
      return items;
    }
    const direction = this.sortDirection() === 'asc' ? 1 : -1;
    return items.sort((a, b) => a[column].localeCompare(b[column]) * direction);
  });

  ngOnInit(): void {
    this.loadVehicles();

    this.plateFilterControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe(() => {
        this.page.set(0);
        this.loadVehicles();
      });
  }

  loadVehicles(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    const plate = sanitizePlate(this.plateFilterControl.value);

    this.vehicleService.getVehicles(plate, this.page(), this.size()).subscribe({
      next: (response) => {
        this.vehicles.set(response.content);
        this.totalPages.set(response.totalPages);
        this.totalElements.set(response.totalElements);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Could not load the vehicle list. Please try again.');
        this.loading.set(false);
      }
    });
  }

  sortBy(column: SortColumn): void {
    if (this.sortColumn() === column) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortColumn.set(column);
      this.sortDirection.set('asc');
    }
  }

  goToPage(newPage: number): void {
    if (newPage < 0 || newPage >= this.totalPages()) {
      return;
    }
    this.page.set(newPage);
    this.loadVehicles();
  }
}