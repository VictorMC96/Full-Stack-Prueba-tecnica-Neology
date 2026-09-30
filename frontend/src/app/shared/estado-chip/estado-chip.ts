import { Component, computed, input } from '@angular/core';

/** Códigos con color propio; cualquier otro código de tipo usa el color neutro. */
const CLASES: Record<string, string> = {
  OFICIAL: 'oficial',
  RESIDENTE: 'residente',
  NO_RESIDENTE: 'no-residente',
  DENTRO: 'dentro',
  FUERA: 'fuera',
};

const ETIQUETAS: Record<string, string> = {
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
  /** Código: `DENTRO`, `FUERA` o el código de un tipo de vehículo. */
  readonly estado = input.required<string>();
  /** Nombre legible del tipo (`tipoNombre`); si falta, se usa el nombre conocido o el código. */
  readonly nombre = input<string | null | undefined>(undefined);

  readonly etiqueta = computed(() => {
    const nombre = this.nombre()?.trim();
    return nombre || ETIQUETAS[this.estado()] || this.estado();
  });
  readonly clase = computed(() => CLASES[this.estado()] ?? 'neutro');
}
