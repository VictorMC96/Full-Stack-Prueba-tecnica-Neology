import { formatCurrency } from '@angular/common';
import { EstanciaResponse, MomentoCobro } from '../../core/modelos/modelos';

export const ETIQUETA_COBRO: Record<MomentoCobro, string> = {
  AL_SALIR: 'Cobrar al salir',
  INFORME_MENSUAL: 'Se acumula al informe mensual',
  NINGUNO: 'Sin cobro',
};

/** Texto de snackbar tras una salida: placa, minutos, importe y momento de cobro. */
export function resumenSalida(estancia: EstanciaResponse, locale: string): string {
  const importe = formatCurrency(estancia.importe ?? 0, locale, '$', 'MXN');
  return `Salida registrada: ${estancia.placa} · ${estancia.minutos ?? 0} min · ${importe} · ${ETIQUETA_COBRO[estancia.momentoCobro]}`;
}
