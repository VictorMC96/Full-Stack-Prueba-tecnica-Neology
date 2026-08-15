import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { MaterialModule } from '../../shared/material.module';
import { OperacionesComponent } from './operaciones.component';

const routes: Routes = [{ path: '', component: OperacionesComponent }];

@NgModule({
  declarations: [OperacionesComponent],
  imports: [CommonModule, ReactiveFormsModule, MaterialModule, RouterModule.forChild(routes)],
})
export class OperacionesModule {}
