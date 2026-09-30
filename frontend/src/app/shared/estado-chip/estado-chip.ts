import { Component, computed, input } from '@angular/core';

export type EstadoChipValor = 'OFICIAL' | 'RESIDENTE' | 'NO_RESIDENTE' | 'DENTRO' | 'FUERA';

const ETIQUETAS: Record<EstadoChipValor, string> = {
  OFICIAL: 'Oficial',
  RESIDENTE: 'Residente',
  NO_RESIDENTE: 'No residente',
  DENTRO: 'Dentro',
  FUERA: 'Fuera',
};

@Component({
  selector: 'app-estado-chip',
  template: `<span [class]="'chip chip--' + clase()">{{ etiqueta() }}</span>`,
  styleUrl: './estado-chip.scss',
})
export class EstadoChip {
  readonly estado = input.required<EstadoChipValor>();
  readonly etiqueta = computed(() => ETIQUETAS[this.estado()]);
  readonly clase = computed(() => this.estado().toLowerCase().replace('_', '-'));
}
