import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { VehiculoDetalle } from '../../../core/models/vehiculo.model';
import { VehiculoService } from '../../../core/services/vehiculo.service';

@Component({
  selector: 'app-vehiculo-detalle',
  templateUrl: './vehiculo-detalle.component.html',
  styleUrls: ['./vehiculo-detalle.component.scss'],
})
export class VehiculoDetalleComponent implements OnInit {
  readonly displayedColumns = ['fechaEntrada', 'fechaSalida', 'cobro'];

  vehiculo: VehiculoDetalle | null = null;
  cargando = false;
  noEncontrado = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly vehiculoService: VehiculoService,
  ) {}

  ngOnInit(): void {
    const placa = this.route.snapshot.paramMap.get('placa');
    if (!placa) {
      this.noEncontrado = true;
      return;
    }
    this.cargar(placa);
  }

  private cargar(placa: string): void {
    this.cargando = true;
    this.vehiculoService
      .obtenerDetalle(placa)
      .pipe(finalize(() => (this.cargando = false)))
      .subscribe({
        next: (detalle) => (this.vehiculo = detalle),
        error: () => (this.noEncontrado = true),
      });
  }
}
