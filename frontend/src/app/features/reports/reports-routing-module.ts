import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ResidentReportComponent } from './pages/resident-report/resident-report.component';

const routes: Routes = [
  {
    path: '',
    component: ResidentReportComponent
  }
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [
    RouterModule
  ]
})
export class ReportsRoutingModule {}