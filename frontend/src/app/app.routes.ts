import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'vehiculos',
    pathMatch: 'full'
  },
  {
    path: 'vehiculos',
    loadChildren: () =>
      import('./features/vehicles/vehicles-module')
        .then(m => m.VehiclesModule)
  },
  {
    path: 'operaciones',
    loadChildren: () =>
      import('./features/operations/operations-module')
        .then(m => m.OperationsModule)
  },
  {
    path: 'reportes',
    loadChildren: () =>
      import('./features/reports/reports-module')
        .then(m => m.ReportsModule)
  },
  {
    path: '**',
    redirectTo: 'vehiculos'
  }
];