import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormGroupDirective, ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of } from 'rxjs';
import { Estancia } from '../../core/models/vehiculo.model';
import { EstanciaService } from '../../core/services/estancia.service';
import { NuevoMesActionService } from '../../core/services/nuevo-mes-action.service';
import { VehiculoService } from '../../core/services/vehiculo.service';
import { MaterialModule } from '../../shared/material.module';
import { OperacionesComponent } from './operaciones.component';

describe('OperacionesComponent', () => {
  let component: OperacionesComponent;
  let fixture: ComponentFixture<OperacionesComponent>;
  let estanciaServiceSpy: jasmine.SpyObj<EstanciaService>;
  let vehiculoServiceSpy: jasmine.SpyObj<VehiculoService>;
  /** No hace falta un FormGroupDirective real: los componentes solo llaman a resetForm() en él. */
  const formDirStub = jasmine.createSpyObj<FormGroupDirective>('FormGroupDirective', ['resetForm']);

  beforeEach(async () => {
    estanciaServiceSpy = jasmine.createSpyObj('EstanciaService', ['registrarEntrada', 'registrarSalida']);
    vehiculoServiceSpy = jasmine.createSpyObj('VehiculoService', ['altaPorTipo']);

    await TestBed.configureTestingModule({
      declarations: [OperacionesComponent],
      imports: [ReactiveFormsModule, NoopAnimationsModule, MaterialModule],
      providers: [
        { provide: EstanciaService, useValue: estanciaServiceSpy },
        { provide: VehiculoService, useValue: vehiculoServiceSpy },
        { provide: NuevoMesActionService, useValue: jasmine.createSpyObj('NuevoMesActionService', ['ejecutar']) },
        { provide: MatSnackBar, useValue: jasmine.createSpyObj('MatSnackBar', ['open']) },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(OperacionesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should not call the API when the placa is invalid', () => {
    component.entradaForm.controls.placa.setValue('');
    component.registrarEntrada(formDirStub);
    expect(estanciaServiceSpy.registrarEntrada).not.toHaveBeenCalled();
    expect(component.entradaForm.controls.placa.touched).toBeTrue();
  });

  it('should reject placas with characters outside letters/numbers/dashes', () => {
    component.entradaForm.controls.placa.setValue('<script>');
    expect(component.entradaForm.controls.placa.hasError('pattern')).toBeTrue();
  });

  it('should normalize and submit the placa on valid entrada', () => {
    const estancia: Estancia = { id: 1, placa: 'ABC123', fechaEntrada: '', fechaSalida: null, cobro: 0 };
    estanciaServiceSpy.registrarEntrada.and.returnValue(of(estancia));

    component.entradaForm.controls.placa.setValue('abc123');
    component.registrarEntrada(formDirStub);

    expect(estanciaServiceSpy.registrarEntrada).toHaveBeenCalledWith('ABC123');
  });

  it('should submit alta with the selected tipo', () => {
    vehiculoServiceSpy.altaPorTipo.and.returnValue(of({ placa: 'ABC123', tipo: 'OFICIAL' }));

    component.altaForm.controls.placa.setValue('abc123');
    component.altaForm.controls.tipo.setValue('OFICIAL');
    component.darDeAlta(formDirStub);

    expect(vehiculoServiceSpy.altaPorTipo).toHaveBeenCalledWith('ABC123', 'OFICIAL');
  });
});
