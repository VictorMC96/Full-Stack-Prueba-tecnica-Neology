import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ResidentePago } from '../../../core/models/residente.model';
import { ResidenteService } from '../../../core/services/residente.service';
import { ResidentesReporteComponent } from './residentes-reporte.component';

describe('ResidentesReporteComponent', () => {
  let component: ResidentesReporteComponent;
  let fixture: ComponentFixture<ResidentesReporteComponent>;
  let residenteServiceSpy: jasmine.SpyObj<ResidenteService>;

  const informe: ResidentePago[] = [
    { placa: 'ABC123', tiempoAcumuladoMinutos: 100, pagoAcumulado: 5 },
    { placa: 'XYZ999', tiempoAcumuladoMinutos: 200, pagoAcumulado: 10 },
  ];

  beforeEach(async () => {
    residenteServiceSpy = jasmine.createSpyObj('ResidenteService', ['informePagos']);
    residenteServiceSpy.informePagos.and.returnValue(of(informe));

    await TestBed.configureTestingModule({
      declarations: [ResidentesReporteComponent],
      providers: [{ provide: ResidenteService, useValue: residenteServiceSpy }],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(ResidentesReporteComponent);
    component = fixture.componentInstance;
  });

  it('should load the payment report on init', () => {
    fixture.detectChanges();
    expect(residenteServiceSpy.informePagos).toHaveBeenCalled();
    expect(component.dataSource.data).toEqual(informe);
  });

  it('should sum the total accumulated payment', () => {
    fixture.detectChanges();
    expect(component.totalPagoAcumulado).toBe(15);
  });
});
