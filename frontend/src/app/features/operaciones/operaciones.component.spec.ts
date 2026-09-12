import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { OperacionesComponent } from './operaciones.component';

describe('OperacionesComponent', () => {
  let http: HttpTestingController;
  let fixture: ReturnType<typeof TestBed.createComponent<OperacionesComponent>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperacionesComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([]), provideNoopAnimations()],
    }).compileComponents();
    http = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(OperacionesComponent);
    fixture.detectChanges();
  });

  afterEach(() => http.verify());

  it('marca como invalida una placa con caracteres no permitidos', () => {
    const campo = fixture.componentInstance.formularioEntrada.controls.placa;

    campo.setValue('<script>');

    expect(campo.valid).toBeFalse();
  });

  it('acepta una placa con letras, numeros y guion', () => {
    const campo = fixture.componentInstance.formularioEntrada.controls.placa;

    campo.setValue('ABC-123');

    expect(campo.valid).toBeTrue();
  });

  it('no envia la entrada mientras el formulario sea invalido', () => {
    fixture.componentInstance.formularioEntrada.controls.placa.setValue('a');

    fixture.componentInstance.registrarEntrada();

    http.expectNone('/neo/estancias/entrada');
  });

  it('envia la entrada cuando el formulario es valido', () => {
    fixture.componentInstance.formularioEntrada.controls.placa.setValue('ABC-123');

    fixture.componentInstance.registrarEntrada();

    const peticion = http.expectOne('/neo/estancias/entrada');
    expect(peticion.request.body.placa).toBe('ABC-123');
    peticion.flush({});
  });

  it('da de alta un vehiculo en la ruta que corresponde a su tipo', () => {
    const formulario = fixture.componentInstance.formularioAlta;
    formulario.controls.placa.setValue('RES-777');
    formulario.controls.propietario.setValue('Aldo');
    formulario.controls.tipo.setValue('RESIDENTE');

    fixture.componentInstance.darDeAlta();

    http.expectOne('/neo/vehiculos/residentes').flush({});
  });
});
