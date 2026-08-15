import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { VehiculoDetalleComponent } from './vehiculo-detalle/vehiculo-detalle.component';
import { VehiculosListadoComponent } from './vehiculos-listado/vehiculos-listado.component';

const routes: Routes = [
  { path: '', component: VehiculosListadoComponent },
  { path: ':placa', component: VehiculoDetalleComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class VehiculosRoutingModule {}
