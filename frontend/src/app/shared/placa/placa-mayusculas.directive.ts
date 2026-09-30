import { Directive, inject } from '@angular/core';
import { NgControl } from '@angular/forms';

/** Pasa a mayúsculas el valor del campo de placa mientras se escribe. */
@Directive({
  selector: 'input[appPlacaMayusculas]',
  host: { '(input)': 'alEscribir($event)' },
})
export class PlacaMayusculas {
  private readonly ngControl = inject(NgControl, { optional: true, self: true });

  alEscribir(evento: Event): void {
    const input = evento.target as HTMLInputElement;
    const mayusculas = input.value.toUpperCase();
    if (mayusculas === input.value) {
      return;
    }
    const inicio = input.selectionStart;
    const fin = input.selectionEnd;
    input.value = mayusculas;
    this.ngControl?.control?.setValue(mayusculas);
    if (inicio !== null && fin !== null) {
      input.setSelectionRange(inicio, fin);
    }
  }
}
