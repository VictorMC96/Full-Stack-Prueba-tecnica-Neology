import { Routes } from '@angular/router';
import { VehicleList } from './vehicle-list/vehicle-list';
import { VehicleDetail } from './vehicle-detail/vehicle-detail';
import { VehicleRegisterForm } from './vehicle-register-form/vehicle-register-form';
import { VehicleEntryForm } from './vehicle-entry-form/vehicle-entry-form';
import { VehicleExitForm } from './vehicle-exit-form/vehicle-exit-form';
import { ResidentReport } from './resident-report/resident-report';

export const routes: Routes = [
  { path: '', redirectTo: 'vehicles', pathMatch: 'full' },
  { path: 'vehicles/new', component: VehicleRegisterForm },
  { path: 'vehicles', component: VehicleList },
  { path: 'vehicles/:plate', component: VehicleDetail },

  { path: 'entries/new', component: VehicleEntryForm },
  { path: 'exits/new', component: VehicleExitForm },
  { path: 'residents/report', component: ResidentReport },
];