import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { EstanciaService } from '../../../core/services/estancia.service';
import { VehiculoService } from '../../../core/services/vehiculo.service';
import { Vehiculo } from '../../../core/models/vehiculo.model';

@Component({
  selector: 'app-vehiculos-listado',
  templateUrl: './vehiculos-listado.component.html',
  styleUrls: ['./vehiculos-listado.component.scss'],
})
export class VehiculosListadoComponent implements OnInit, AfterViewInit {
  readonly displayedColumns = ['placa', 'tipo', 'acciones'];
  readonly dataSource = new MatTableDataSource<Vehiculo>([]);

  cargando = false;
  /** Placa para la que hay una acción de entrada/salida en curso, para deshabilitar solo esos botones. */
  placaEnProceso: string | null = null;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private readonly vehiculoService: VehiculoService,
    private readonly estanciaService: EstanciaService,
    private readonly snackBar: MatSnackBar,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.dataSource.filterPredicate = (vehiculo, filtro) => vehiculo.placa.toLowerCase().includes(filtro);
    this.cargar();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  cargar(): void {
    this.cargando = true;
    this.vehiculoService
      .listar()
      .pipe(finalize(() => (this.cargando = false)))
      .subscribe((vehiculos) => (this.dataSource.data = vehiculos));
  }

  aplicarFiltro(valor: string): void {
    this.dataSource.filter = valor.trim().toLowerCase();
  }

  verDetalle(placa: string): void {
    this.router.navigate(['/vehiculos', placa]);
  }

  registrarEntrada(placa: string): void {
    this.placaEnProceso = placa;
    this.estanciaService
      .registrarEntrada(placa)
      .pipe(finalize(() => (this.placaEnProceso = null)))
      .subscribe(() => {
        this.snackBar.open(`Entrada registrada para ${placa}.`, 'Cerrar', { duration: 3000 });
      });
  }

  registrarSalida(placa: string): void {
    this.placaEnProceso = placa;
    this.estanciaService
      .registrarSalida(placa)
      .pipe(finalize(() => (this.placaEnProceso = null)))
      .subscribe((estancia) => {
        this.snackBar.open(
          `Salida registrada para ${placa}. Cobro: $${estancia.cobro.toFixed(2)}`,
          'Cerrar',
          { duration: 4000 },
        );
      });
  }
}
