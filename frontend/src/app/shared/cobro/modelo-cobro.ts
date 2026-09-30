import { ModeloCobro } from '../../core/modelos/modelos';

export const ETIQUETA_MODELO_COBRO: Record<ModeloCobro, string> = {
  SIN_COBRO: 'Sin cobro',
  POR_MINUTO_AL_SALIR: 'Por minuto al salir',
  POR_MINUTO_ACUMULADO: 'Por minuto, acumulado al informe',
};

export const OPCIONES_MODELO_COBRO: { valor: ModeloCobro; etiqueta: string }[] = (
  Object.keys(ETIQUETA_MODELO_COBRO) as ModeloCobro[]
).map((valor) => ({ valor, etiqueta: ETIQUETA_MODELO_COBRO[valor] }));

export function etiquetaModeloCobro(modelo: string): string {
  return ETIQUETA_MODELO_COBRO[modelo as ModeloCobro] ?? modelo;
}
