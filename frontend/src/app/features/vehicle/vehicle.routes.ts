import { Routes } from '@angular/router';

export const VEHICLE_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/vehicle-page/vehicle-page')
        .then((m) => m.VehiclePageComponent),
    title: 'Vehículos',
  },
];