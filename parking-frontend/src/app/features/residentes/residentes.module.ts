import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MaterialModule } from '../../shared/material.module';
import { ResidentesReporteComponent } from './residentes-reporte/residentes-reporte.component';

const routes: Routes = [{ path: '', component: ResidentesReporteComponent }];

@NgModule({
  declarations: [ResidentesReporteComponent],
  imports: [CommonModule, MaterialModule, RouterModule.forChild(routes)],
})
export class ResidentesModule {}
