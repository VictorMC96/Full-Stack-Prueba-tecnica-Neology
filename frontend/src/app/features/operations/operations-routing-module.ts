import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ParkingOperationsComponent } from './pages/parking-operations/parking-operations.component';


const routes: Routes = [
  {
    path: '',
    component: ParkingOperationsComponent
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
export class OperationsRoutingModule {}