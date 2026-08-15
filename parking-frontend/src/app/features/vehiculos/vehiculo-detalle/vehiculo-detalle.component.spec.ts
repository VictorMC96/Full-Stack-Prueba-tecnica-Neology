import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { of, throwError } from 'rxjs';
import { VehiculoDetalle } from '../../../core/models/vehiculo.model';
import { VehiculoService } from '../../../core/services/vehiculo.service';
import { VehiculoDetalleComponent } from './vehiculo-detalle.component';

describe('VehiculoDetalleComponent', () => {
  let component: VehiculoDetalleComponent;
  let fixture: ComponentFixture<VehiculoDetalleComponent>;
  let vehiculoServiceSpy: jasmine.SpyObj<VehiculoService>;

  const detalle: VehiculoDetalle = {
    placa: 'ABC123',
    tipo: 'RESIDENTE',
    estancias: [],
    tiempoAcumuladoMinutos: 120,
    pagoAcumulado: 6,
  };

  function crearComponente(placa: string | null) {
    TestBed.configureTestingModule({
      declarations: [VehiculoDetalleComponent],
      providers: [
        { provide: VehiculoService, useValue: vehiculoServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap(placa ? { placa } : {}) } },
        },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    });
    fixture = TestBed.createComponent(VehiculoDetalleComponent);
    component = fixture.componentInstance;
  }

  beforeEach(() => {
    vehiculoServiceSpy = jasmine.createSpyObj('VehiculoService', ['obtenerDetalle']);
  });

  it('should load the vehicle detail for the placa in the route', () => {
    vehiculoServiceSpy.obtenerDetalle.and.returnValue(of(detalle));
    crearComponente('ABC123');

    fixture.detectChanges();

    expect(vehiculoServiceSpy.obtenerDetalle).toHaveBeenCalledWith('ABC123');
    expect(component.vehiculo).toEqual(detalle);
    expect(component.noEncontrado).toBeFalse();
  });

  it('should flag noEncontrado when the backend returns an error', () => {
    vehiculoServiceSpy.obtenerDetalle.and.returnValue(throwError(() => new Error('404')));
    crearComponente('ZZZ999');

    fixture.detectChanges();

    expect(component.noEncontrado).toBeTrue();
    expect(component.vehiculo).toBeNull();
  });
});
