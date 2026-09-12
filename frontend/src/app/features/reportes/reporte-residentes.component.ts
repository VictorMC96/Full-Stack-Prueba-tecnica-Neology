import { Component, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ApiService } from '../../core/api.service';
import { InformePagos } from '../../core/modelos';

@Component({
  selector: 'app-reporte-residentes',
  standalone: true,
  imports: [DecimalPipe, MatTableModule, MatCardModule, MatButtonModule, MatIconModule, MatProgressBarModule],
  templateUrl: './reporte-residentes.component.html',
  styleUrl: './reporte-residentes.component.scss',
})
//Componente-Reporte
export class ReporteResidentesComponent {
  private readonly api = inject(ApiService);

  readonly columnas = ['placa', 'propietario', 'minutos', 'monto'];
  readonly informe = signal<InformePagos | null>(null);
  readonly cargando = signal(true);

  constructor() {
    this.consultar();
  }

  //Consulta-Informe
  consultar(): void {
    this.cargando.set(true);
    this.api.informePagos().subscribe({
      next: informe => {
        this.informe.set(informe);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  //Impresion-Informe
  imprimir(): void {
    window.print();
  }
}
