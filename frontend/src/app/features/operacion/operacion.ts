import { Component } from '@angular/core';
import { AltaForm } from './alta-form';
import { IniciarMes } from './iniciar-mes';
import { MovimientoForm } from './movimiento-form';

@Component({
  selector: 'app-operacion',
  imports: [MovimientoForm, AltaForm, IniciarMes],
  templateUrl: './operacion.html',
  styleUrl: './operacion.scss',
})
export class Operacion {}
