import { Routes } from '@angular/router';
import { MainLayoutComponent } from './commond/layouts/main-layout.component/main-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: 'parking-lot',
        loadChildren: () =>
          import('./features/parking-lot/parking-log.routes').then((m) => m.parkingLotRoutes),
      },
      {
        path: 'vehicles',
        loadChildren: () =>
          import('./features/vehicle/vehicle.routes').then((m) => m.VEHICLE_ROUTES),
      },
      {
        path: '',
        redirectTo: 'parking-lot',
        pathMatch: 'full',
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'parking-lot',
  },
];