import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'vehiculos' },
  {
    path: 'vehiculos',
    title: 'Vehículos · Gestión de Acceso Vehicular',
    loadComponent: () =>
      import('./features/vehiculos/lista/vehiculos-lista').then((m) => m.VehiculosLista),
  },
  {
    path: 'vehiculos/:placa',
    title: 'Detalle de vehículo · Gestión de Acceso Vehicular',
    loadComponent: () =>
      import('./features/vehiculos/detalle/vehiculo-detalle').then((m) => m.VehiculoDetalle),
  },
  {
    path: 'operacion',
    title: 'Operación · Gestión de Acceso Vehicular',
    loadComponent: () => import('./features/operacion/operacion').then((m) => m.Operacion),
  },
  {
    path: 'reporte',
    title: 'Reporte de residentes · Gestión de Acceso Vehicular',
    loadComponent: () => import('./features/reporte/reporte').then((m) => m.Reporte),
  },
  {
    path: '**',
    title: 'Página no encontrada · Gestión de Acceso Vehicular',
    loadComponent: () =>
      import('./features/no-encontrado/no-encontrado').then((m) => m.NoEncontrado),
  },
];
