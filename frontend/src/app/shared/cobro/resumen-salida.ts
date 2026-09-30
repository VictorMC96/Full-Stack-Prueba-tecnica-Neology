import { formatCurrency } from '@angular/common';
import { EstanciaResponse, MomentoCobro } from '../../core/modelos/modelos';
import { formatearDuracion } from '../duracion.pipe';

export const ETIQUETA_COBRO: Record<MomentoCobro, string> = {
  AL_SALIR: 'Cobrar al salir',
  INFORME_MENSUAL: 'Se acumula al informe mensual',
  NINGUNO: 'Sin cobro',
};

/** Texto de snackbar tras una salida: placa, duración real, minutos cobrados, importe y cobro. */
export function resumenSalida(estancia: EstanciaResponse, locale: string): string {
  const duracion = formatearDuracion(estancia.duracionSegundos);
  const importe = formatCurrency(estancia.importe ?? 0, locale, '$', 'MXN');
  return `Salida registrada: ${estancia.placa} · ${duracion} · ${estancia.minutos ?? 0} min cobrados · ${importe} · ${ETIQUETA_COBRO[estancia.momentoCobro]}`;
}
