import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'vehiculos', pathMatch: 'full' },
  {
    path: 'vehiculos',
    loadComponent: () =>
      import('./features/vehiculos/listado-vehiculos.component').then(m => m.ListadoVehiculosComponent),
  },
  {
    path: 'vehiculos/:placa',
    loadComponent: () =>
      import('./features/vehiculos/detalle-vehiculo.component').then(m => m.DetalleVehiculoComponent),
  },
  {
    path: 'operaciones',
    loadComponent: () =>
      import('./features/operaciones/operaciones.component').then(m => m.OperacionesComponent),
  },
  {
    path: 'reportes',
    loadComponent: () =>
      import('./features/reportes/reporte-residentes.component').then(m => m.ReporteResidentesComponent),
  },
  { path: '**', redirectTo: 'vehiculos' },
];
