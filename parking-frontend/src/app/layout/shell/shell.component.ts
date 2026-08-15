import { Component } from '@angular/core';
import { NuevoMesActionService } from '../../core/services/nuevo-mes-action.service';

@Component({
  selector: 'app-shell',
  templateUrl: './shell.component.html',
  styleUrls: ['./shell.component.scss'],
})
export class ShellComponent {
  iniciandoMes = false;

  readonly links = [
    { path: '/vehiculos', label: 'Vehículos', icon: 'directions_car' },
    { path: '/operaciones', label: 'Operaciones', icon: 'assignment' },
    { path: '/residentes', label: 'Reporte residentes', icon: 'summarize' },
  ];

  constructor(private readonly nuevoMesAction: NuevoMesActionService) {}

  iniciarNuevoMes(): void {
    this.nuevoMesAction.ejecutar((enProceso) => (this.iniciandoMes = enProceso)).subscribe();
  }
}
