import { Component } from '@angular/core';
import { FormBuilder, FormGroupDirective, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { finalize } from 'rxjs/operators';
import { EstanciaService } from '../../core/services/estancia.service';
import { NuevoMesActionService } from '../../core/services/nuevo-mes-action.service';
import { TipoVehiculo } from '../../core/models/vehiculo.model';
import { VehiculoService } from '../../core/services/vehiculo.service';

/** Solo letras, números y guiones: evita que se cuelen etiquetas/scripts en la placa. */
const PLACA_VALIDATORS = [Validators.required, Validators.maxLength(10), Validators.pattern(/^[A-Za-z0-9-]+$/)];

@Component({
  selector: 'app-operaciones',
  templateUrl: './operaciones.component.html',
  styleUrls: ['./operaciones.component.scss'],
})
export class OperacionesComponent {
  readonly tiposVehiculo: { value: TipoVehiculo; label: string }[] = [
    { value: 'OFICIAL', label: 'Oficial (no paga)' },
    { value: 'RESIDENTE', label: 'Residente ($0.05/min acumulado)' },
    { value: 'NO_RESIDENTE', label: 'No residente ($0.5/min al salir)' },
  ];

  readonly entradaForm = this.fb.nonNullable.group({
    placa: ['', PLACA_VALIDATORS],
  });

  readonly salidaForm = this.fb.nonNullable.group({
    placa: ['', PLACA_VALIDATORS],
  });

  readonly altaForm = this.fb.nonNullable.group({
    placa: ['', PLACA_VALIDATORS],
    tipo: ['RESIDENTE' as TipoVehiculo, Validators.required],
  });

  enviandoEntrada = false;
  enviandoSalida = false;
  enviandoAlta = false;
  iniciandoMes = false;

  constructor(
    private readonly fb: FormBuilder,
    private readonly estanciaService: EstanciaService,
    private readonly vehiculoService: VehiculoService,
    private readonly nuevoMesAction: NuevoMesActionService,
    private readonly snackBar: MatSnackBar,
  ) {}

  registrarEntrada(formDir: FormGroupDirective): void {
    if (this.entradaForm.invalid) {
      this.entradaForm.markAllAsTouched();
      return;
    }
    const placa = this.normalizarPlaca(this.entradaForm.getRawValue().placa);
    this.enviandoEntrada = true;
    this.estanciaService
      .registrarEntrada(placa)
      .pipe(finalize(() => (this.enviandoEntrada = false)))
      .subscribe(() => {
        this.snackBar.open(`Entrada registrada para ${placa}.`, 'Cerrar', { duration: 3000 });
        formDir.resetForm();
      });
  }

  registrarSalida(formDir: FormGroupDirective): void {
    if (this.salidaForm.invalid) {
      this.salidaForm.markAllAsTouched();
      return;
    }
    const placa = this.normalizarPlaca(this.salidaForm.getRawValue().placa);
    this.enviandoSalida = true;
    this.estanciaService
      .registrarSalida(placa)
      .pipe(finalize(() => (this.enviandoSalida = false)))
      .subscribe((estancia) => {
        this.snackBar.open(
          `Salida registrada para ${placa}. Cobro: $${estancia.cobro.toFixed(2)}`,
          'Cerrar',
          { duration: 4000 },
        );
        formDir.resetForm();
      });
  }

  darDeAlta(formDir: FormGroupDirective): void {
    if (this.altaForm.invalid) {
      this.altaForm.markAllAsTouched();
      return;
    }
    const { placa, tipo } = this.altaForm.getRawValue();
    const placaNormalizada = this.normalizarPlaca(placa);
    this.enviandoAlta = true;
    this.vehiculoService
      .altaPorTipo(placaNormalizada, tipo)
      .pipe(finalize(() => (this.enviandoAlta = false)))
      .subscribe(() => {
        this.snackBar.open(`Vehículo ${placaNormalizada} dado de alta como ${tipo}.`, 'Cerrar', {
          duration: 3000,
        });
        formDir.resetForm({ placa: '', tipo: 'RESIDENTE' });
      });
  }

  iniciarNuevoMes(): void {
    this.nuevoMesAction.ejecutar((enProceso) => (this.iniciandoMes = enProceso)).subscribe();
  }

  private normalizarPlaca(placa: string): string {
    return placa.trim().toUpperCase();
  }
}
