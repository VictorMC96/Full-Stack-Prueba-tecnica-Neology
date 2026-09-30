import { BreakpointObserver } from '@angular/cdk/layout';
import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { map } from 'rxjs';

interface Enlace {
  ruta: string;
  etiqueta: string;
  icono: string;
}

@Component({
  selector: 'app-shell',
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatSidenavModule,
    MatToolbarModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
  ],
  templateUrl: './shell.html',
  styleUrl: './shell.scss',
})
export class Shell {
  private readonly breakpoints = inject(BreakpointObserver);

  readonly enlaces: Enlace[] = [
    { ruta: '/vehiculos', etiqueta: 'Vehículos', icono: 'directions_car' },
    { ruta: '/operacion', etiqueta: 'Operación', icono: 'swap_horiz' },
    { ruta: '/reporte', etiqueta: 'Reporte', icono: 'receipt_long' },
    { ruta: '/tipos-vehiculo', etiqueta: 'Tipos de vehículo', icono: 'category' },
  ];

  readonly esMovil = toSignal(
    this.breakpoints.observe('(max-width: 599.98px)').pipe(map((estado) => estado.matches)),
    { initialValue: false },
  );
}
