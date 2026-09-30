import { CurrencyPipe } from '@angular/common';
import { Component, inject, signal, viewChild } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormGroupDirective,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { filter, switchMap } from 'rxjs';
import { TiposVehiculoApi } from '../../core/api/tipos-vehiculo-api';
import { TipoVehiculoEdicion, TipoVehiculoResponse } from '../../core/modelos/modelos';
import { OPCIONES_MODELO_COBRO, etiquetaModeloCobro } from '../../shared/cobro/modelo-cobro';
import {
  ConfirmarDialog,
  ConfirmarDialogDatos,
} from '../../shared/confirmar-dialog/confirmar-dialog';
import { EmptyState } from '../../shared/empty-state/empty-state';
import { EstadoChip } from '../../shared/estado-chip/estado-chip';
import { Loading } from '../../shared/loading/loading';
import { PlacaMayusculas } from '../../shared/placa/placa-mayusculas.directive';
import { TipoDialog } from './tipo-dialog';
import { camposTipo, codigoTipoValidator, enlazarTarifaAlModelo } from './tipo-form';

@Component({
  selector: 'app-tipos-vehiculo',
  imports: [
    CurrencyPipe,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    MatTableModule,
    MatTooltipModule,
    EmptyState,
    EstadoChip,
    Loading,
    PlacaMayusculas,
  ],
  templateUrl: './tipos-vehiculo.html',
  styleUrl: './tipos-vehiculo.scss',
})
export class TiposVehiculo {
  private readonly api = inject(TiposVehiculoApi);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  private readonly formDir = viewChild(FormGroupDirective);

  readonly columnas = [
    'codigo',
    'nombre',
    'modeloCobro',
    'tarifaPorMinuto',
    'vehiculos',
    'estado',
    'acciones',
  ];
  readonly modelos = OPCIONES_MODELO_COBRO;
  readonly etiquetaModelo = etiquetaModeloCobro;

  readonly tipos = signal<TipoVehiculoResponse[] | null>(null);
  readonly cargando = signal(false);
  readonly error = signal(false);
  readonly enviando = signal(false);
  /** Código del tipo que se está editando o eliminando. */
  readonly codigoEnCurso = signal<string | null>(null);

  readonly form = new FormGroup({
    codigo: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, codigoTipoValidator],
    }),
    ...camposTipo(),
  });

  constructor() {
    enlazarTarifaAlModelo(this.form);
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.error.set(false);
    this.api.listar().subscribe({
      next: (tipos) => {
        this.tipos.set(tipos);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set(true);
        this.cargando.set(false);
      },
    });
  }

  crear(): void {
    if (this.form.invalid || this.enviando()) {
      this.form.markAllAsTouched();
      return;
    }
    const { codigo, nombre, modeloCobro, tarifaPorMinuto } = this.form.getRawValue();
    this.enviando.set(true);
    this.api
      .crear({
        codigo: codigo.toUpperCase().trim(),
        nombre: nombre.trim(),
        modeloCobro,
        tarifaPorMinuto,
      })
      .subscribe({
        next: (tipo) => {
          this.enviando.set(false);
          this.snackBar.open(`Tipo ${tipo.codigo} creado`, 'Cerrar', { duration: 4000 });
          this.formDir()?.resetForm({ codigo: '', nombre: '', modeloCobro, tarifaPorMinuto: 0 });
          this.cargar();
        },
        error: () => this.enviando.set(false),
      });
  }

  editar(tipo: TipoVehiculoResponse): void {
    if (this.codigoEnCurso()) {
      return;
    }
    this.dialog
      .open<TipoDialog, TipoVehiculoResponse, TipoVehiculoEdicion>(TipoDialog, {
        data: tipo,
        width: '480px',
        autoFocus: 'first-tabbable',
        restoreFocus: true,
      })
      .afterClosed()
      .pipe(
        filter((datos): datos is TipoVehiculoEdicion => !!datos),
        switchMap((datos) => {
          this.codigoEnCurso.set(tipo.codigo);
          return this.api.actualizar(tipo.codigo, datos);
        }),
      )
      .subscribe({
        next: (actualizado) => {
          this.codigoEnCurso.set(null);
          this.snackBar.open(`Tipo ${actualizado.codigo} actualizado`, 'Cerrar', {
            duration: 4000,
          });
          this.cargar();
        },
        error: () => this.codigoEnCurso.set(null),
      });
  }

  motivoNoEliminable(tipo: TipoVehiculoResponse): string | null {
    if (tipo.integrado) {
      return 'Los tipos integrados no se pueden eliminar';
    }
    if (tipo.vehiculos > 0) {
      return `Tiene ${tipo.vehiculos} vehículo${tipo.vehiculos === 1 ? '' : 's'} asignado${tipo.vehiculos === 1 ? '' : 's'}`;
    }
    return null;
  }

  eliminar(tipo: TipoVehiculoResponse): void {
    if (this.codigoEnCurso() || this.motivoNoEliminable(tipo)) {
      return;
    }
    const datos: ConfirmarDialogDatos = {
      titulo: `¿Eliminar el tipo ${tipo.codigo}?`,
      texto: `Se eliminará «${tipo.nombre}». Esta acción no se puede deshacer.`,
      confirmar: 'Eliminar',
      cancelar: 'Cancelar',
    };
    this.dialog
      .open<ConfirmarDialog, ConfirmarDialogDatos, boolean>(ConfirmarDialog, {
        data: datos,
        autoFocus: 'first-tabbable',
        restoreFocus: true,
      })
      .afterClosed()
      .pipe(
        filter((confirmado) => confirmado === true),
        switchMap(() => {
          this.codigoEnCurso.set(tipo.codigo);
          return this.api.eliminar(tipo.codigo);
        }),
      )
      .subscribe({
        next: () => {
          this.codigoEnCurso.set(null);
          this.snackBar.open(`Tipo ${tipo.codigo} eliminado`, 'Cerrar', { duration: 4000 });
          this.cargar();
        },
        error: () => this.codigoEnCurso.set(null),
      });
  }
}
