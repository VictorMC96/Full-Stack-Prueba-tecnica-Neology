import { Routes } from '@angular/router';

export const parkingLotRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/parking-lot-page/parking-lot-page').then((m) => m.ParkingLotPageComponent),
  },
];
