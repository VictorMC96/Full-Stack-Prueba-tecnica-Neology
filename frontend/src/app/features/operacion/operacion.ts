import { Component } from '@angular/core';
import { AltaForm } from './alta-form';
import { EntradaForm } from './entrada-form';
import { IniciarMes } from './iniciar-mes';
import { SalidaForm } from './salida-form';

@Component({
  selector: 'app-operacion',
  imports: [EntradaForm, SalidaForm, AltaForm, IniciarMes],
  templateUrl: './operacion.html',
  styleUrl: './operacion.scss',
})
export class Operacion {}
