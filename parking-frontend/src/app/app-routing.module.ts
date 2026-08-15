import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: '', redirectTo: 'vehiculos', pathMatch: 'full' },
  {
    path: 'vehiculos',
    loadChildren: () => import('./features/vehiculos/vehiculos.module').then((m) => m.VehiculosModule),
  },
  {
    path: 'operaciones',
    loadChildren: () => import('./features/operaciones/operaciones.module').then((m) => m.OperacionesModule),
  },
  {
    path: 'residentes',
    loadChildren: () => import('./features/residentes/residentes.module').then((m) => m.ResidentesModule),
  },
  { path: '**', redirectTo: 'vehiculos' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
