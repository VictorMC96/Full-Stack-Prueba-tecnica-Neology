import { Pipe, PipeTransform } from '@angular/core';

/** Segundos → `mm:ss` con minutos totales sin tope (605 → "10:05", 12600 → "210:00"); null → "—". */
export function formatearDuracion(segundos: number | null | undefined): string {
  if (segundos === null || segundos === undefined || !Number.isFinite(segundos)) {
    return '—';
  }
  const total = Math.max(0, Math.floor(segundos));
  const minutos = Math.floor(total / 60);
  const resto = total % 60;
  return `${minutos}:${resto.toString().padStart(2, '0')}`;
}

@Pipe({ name: 'duracion' })
export class DuracionPipe implements PipeTransform {
  transform(segundos: number | null | undefined): string {
    return formatearDuracion(segundos);
  }
}
