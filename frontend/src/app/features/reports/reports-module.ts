import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ReportsRoutingModule } from './reports-routing-module';
import { ResidentReportComponent } from './pages/resident-report/resident-report.component';
import { SharedModule } from '../../shared/shared-module';

@NgModule({
  declarations: [ResidentReportComponent],
  imports: [CommonModule, ReportsRoutingModule, SharedModule],
})
export class ReportsModule {}
