import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { VehicleListComponent } from './pages/vehicle-list/vehicle-list.component';
import { VehicleDetailComponent } from './pages/vehicle-detail/vehicle-detail.component';


const routes: Routes = [
  {
    path: '',
    component: VehicleListComponent
  },
  {
    path: ':plate',
    component: VehicleDetailComponent
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
export class VehiclesRoutingModule {}