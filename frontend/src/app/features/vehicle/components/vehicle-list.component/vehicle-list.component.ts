import { Component, input, output } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';

import { VehicleListItem } from '../../services/vehicle-service';

@Component({
  selector: 'app-vehicle-list',
  imports: [MatTableModule, MatPaginatorModule, MatButtonModule],
  templateUrl: './vehicle-list.component.html',
})
export class VehicleListComponent {
  readonly vehicles = input.required<VehicleListItem[]>();
  readonly total = input.required<number>();
  readonly pageIndex = input.required<number>();
  readonly pageSize = input.required<number>();
  readonly loading = input<boolean>(false);

  readonly pageChange = output<PageEvent>();
  readonly detail = output<VehicleListItem>();

  protected readonly displayedColumns = [
    'licensePlate',
    'type',
    'brand',
    'model',
    'color',
    'actions',
  ];

  protected onPage(event: PageEvent): void {
    this.pageChange.emit(event);
  }

  protected onDetail(vehicle: VehicleListItem): void {
    this.detail.emit(vehicle);
  }
}