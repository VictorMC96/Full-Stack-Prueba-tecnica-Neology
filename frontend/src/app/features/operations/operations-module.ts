import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { OperationsRoutingModule } from './operations-routing-module';
import { ParkingOperationsComponent } from './pages/parking-operations/parking-operations.component';
import { SharedModule } from '../../shared/shared-module';

@NgModule({
  declarations: [ParkingOperationsComponent],
  imports: [CommonModule, OperationsRoutingModule, SharedModule],
})
export class OperationsModule {}
