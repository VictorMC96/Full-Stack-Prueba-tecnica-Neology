import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { finalize } from 'rxjs/operators';
import { ResidentePago } from '../../../core/models/residente.model';
import { ResidenteService } from '../../../core/services/residente.service';

@Component({
  selector: 'app-residentes-reporte',
  templateUrl: './residentes-reporte.component.html',
  styleUrls: ['./residentes-reporte.component.scss'],
})
export class ResidentesReporteComponent implements OnInit, AfterViewInit {
  readonly displayedColumns = ['placa', 'tiempoAcumuladoMinutos', 'pagoAcumulado'];
  readonly dataSource = new MatTableDataSource<ResidentePago>([]);

  cargando = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private readonly residenteService: ResidenteService) {}

  ngOnInit(): void {
    this.cargar();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  get totalPagoAcumulado(): number {
    return this.dataSource.data.reduce((total, r) => total + r.pagoAcumulado, 0);
  }

  private cargar(): void {
    this.cargando = true;
    this.residenteService
      .informePagos()
      .pipe(finalize(() => (this.cargando = false)))
      .subscribe((residentes) => (this.dataSource.data = residentes));
  }
}
