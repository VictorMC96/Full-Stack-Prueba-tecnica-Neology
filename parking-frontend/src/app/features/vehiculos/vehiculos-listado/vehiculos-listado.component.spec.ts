import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { EstanciaService } from '../../../core/services/estancia.service';
import { Vehiculo } from '../../../core/models/vehiculo.model';
import { VehiculoService } from '../../../core/services/vehiculo.service';
import { VehiculosListadoComponent } from './vehiculos-listado.component';

describe('VehiculosListadoComponent', () => {
  let component: VehiculosListadoComponent;
  let fixture: ComponentFixture<VehiculosListadoComponent>;
  let vehiculoServiceSpy: jasmine.SpyObj<VehiculoService>;

  const vehiculos: Vehiculo[] = [
    { placa: 'ABC123', tipo: 'RESIDENTE' },
    { placa: 'XYZ999', tipo: 'OFICIAL' },
  ];

  beforeEach(async () => {
    vehiculoServiceSpy = jasmine.createSpyObj('VehiculoService', ['listar']);
    vehiculoServiceSpy.listar.and.returnValue(of(vehiculos));

    await TestBed.configureTestingModule({
      declarations: [VehiculosListadoComponent],
      imports: [RouterTestingModule],
      providers: [
        { provide: VehiculoService, useValue: vehiculoServiceSpy },
        { provide: EstanciaService, useValue: jasmine.createSpyObj('EstanciaService', ['registrarEntrada', 'registrarSalida']) },
        { provide: MatSnackBar, useValue: jasmine.createSpyObj('MatSnackBar', ['open']) },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(VehiculosListadoComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should load vehicles on init', () => {
    fixture.detectChanges();
    expect(vehiculoServiceSpy.listar).toHaveBeenCalled();
    expect(component.dataSource.data).toEqual(vehiculos);
  });

  it('should filter by placa (case-insensitive)', () => {
    fixture.detectChanges();
    component.aplicarFiltro('abc');
    expect(component.dataSource.filteredData).toEqual([vehiculos[0]]);
  });
});
