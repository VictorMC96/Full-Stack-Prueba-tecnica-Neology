import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MaterialModule } from '../../shared/material.module';
import { VehiculoDetalleComponent } from './vehiculo-detalle/vehiculo-detalle.component';
import { VehiculosListadoComponent } from './vehiculos-listado/vehiculos-listado.component';
import { VehiculosRoutingModule } from './vehiculos-routing.module';

@NgModule({
  declarations: [VehiculosListadoComponent, VehiculoDetalleComponent],
  imports: [CommonModule, ReactiveFormsModule, RouterModule, MaterialModule, VehiculosRoutingModule],
})
export class VehiculosModule {}
