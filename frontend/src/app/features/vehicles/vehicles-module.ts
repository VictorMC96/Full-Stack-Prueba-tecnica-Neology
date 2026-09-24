import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { VehiclesRoutingModule } from './vehicles-routing-module';
import { VehicleListComponent } from './pages/vehicle-list/vehicle-list.component';
import { VehicleDetailComponent } from './pages/vehicle-detail/vehicle-detail.component';
import { SharedModule } from '../../shared/shared-module';

@NgModule({
  declarations: [VehicleListComponent, VehicleDetailComponent],
  imports: [CommonModule, VehiclesRoutingModule, SharedModule],
})
export class VehiclesModule {}
