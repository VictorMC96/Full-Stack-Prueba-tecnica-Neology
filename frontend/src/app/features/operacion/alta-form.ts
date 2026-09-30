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
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable, catchError, map, of, switchMap } from 'rxjs';
import { EstanciasApi } from '../../core/api/estancias-api';
import { TiposVehiculoApi } from '../../core/api/tipos-vehiculo-api';
import { VehiculosApi } from '../../core/api/vehiculos-api';
import { TipoVehiculoResponse, VehiculoResponse } from '../../core/modelos/modelos';
import { PlacaMayusculas } from '../../shared/placa/placa-mayusculas.directive';
import { normalizarPlaca, placaValidator } from '../../shared/placa/placa.validator';

interface ResultadoAlta {
  vehiculo: VehiculoResponse;
  /** `true` si se registró la entrada; `null` si la entrada se pidió pero falló. */
  entrada: boolean | null;
}

@Component({
  selector: 'app-alta-form',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSlideToggleModule,
    PlacaMayusculas,
  ],
  templateUrl: './alta-form.html',
  styleUrl: './tarjeta-formulario.scss',
})
export class AltaForm {
  private readonly api = inject(VehiculosApi);
  private readonly tiposApi = inject(TiposVehiculoApi);
  private readonly estancias = inject(EstanciasApi);
  private readonly snackBar = inject(MatSnackBar);
  private readonly formDir = viewChild(FormGroupDirective);

  readonly tipos = signal<TipoVehiculoResponse[]>([]);
  readonly cargandoTipos = signal(false);
  readonly errorTipos = signal(false);

  readonly form = new FormGroup({
    placa: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, placaValidator],
    }),
    tipo: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    registrarEntrada: new FormControl(true, { nonNullable: true }),
  });
  readonly enviando = signal(false);

  constructor() {
    this.cargarTipos();
  }

  cargarTipos(): void {
    this.cargandoTipos.set(true);
    this.errorTipos.set(false);
    this.tiposApi.listar().subscribe({
      next: (tipos) => {
        this.tipos.set(tipos);
        this.cargandoTipos.set(false);
        const actual = this.form.controls.tipo.value;
        if (!tipos.some((t) => t.codigo === actual)) {
          const preferido = tipos.find((t) => !t.provisional) ?? tipos[0];
          this.form.controls.tipo.setValue(preferido?.codigo ?? '');
        }
      },
      error: () => {
        this.cargandoTipos.set(false);
        this.errorTipos.set(true);
      },
    });
  }

  enviar(): void {
    if (this.form.invalid || this.enviando()) {
      this.form.markAllAsTouched();
      return;
    }
    const placa = normalizarPlaca(this.form.controls.placa.value);
    const tipo = this.form.controls.tipo.value;
    const registrarEntrada = this.form.controls.registrarEntrada.value;
    this.enviando.set(true);
    this.api
      .alta(tipo, placa)
      .pipe(
        switchMap((vehiculo): Observable<ResultadoAlta> => {
          if (!registrarEntrada) {
            return of({ vehiculo, entrada: false });
          }
          return this.estancias.entrada(vehiculo.placa).pipe(
            map(() => ({ vehiculo, entrada: true })),
            // El interceptor ya avisó del fallo de la entrada; el alta sí se completó.
            catchError(() => of({ vehiculo, entrada: null })),
          );
        }),
      )
      .subscribe({
        next: ({ vehiculo, entrada }) => {
          this.enviando.set(false);
          this.formDir()?.resetForm({ placa: '', tipo, registrarEntrada });
          if (entrada === null) {
            return;
          }
          const nombre =
            vehiculo.tipoNombre || this.tipos().find((t) => t.codigo === tipo)?.nombre || tipo;
          const sufijo = entrada ? ' y entrada registrada' : '';
          this.snackBar.open(
            `Vehículo ${vehiculo.placa} registrado como ${nombre}${sufijo}`,
            'Cerrar',
            { duration: 4000 },
          );
        },
        error: () => this.enviando.set(false),
      });
  }
}
