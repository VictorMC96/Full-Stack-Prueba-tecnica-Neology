package mx.neology.acceso.domain;

import java.util.Locale;
import java.util.regex.Pattern;
import mx.neology.acceso.domain.excepcion.ValidacionException;

/** Código de un tipo de vehículo: mayúsculas, empieza por letra, 3 a 20 caracteres. */
public record CodigoTipo(String valor) {

  private static final Pattern FORMATO = Pattern.compile("^[A-Z][A-Z0-9_]{2,19}$");

  public CodigoTipo {
    if (valor == null || !FORMATO.matcher(valor).matches()) {
      throw new ValidacionException(
          "codigo",
          "El código '"
              + valor
              + "' no es válido: empieza por letra y usa de 3 a 20 letras, dígitos o guiones bajos");
    }
  }

  /** Normaliza (recorta y pasa a mayúsculas) y valida. */
  public static CodigoTipo de(String crudo) {
    if (crudo == null) {
      throw new ValidacionException("codigo", "El código del tipo es obligatorio");
    }
    return new CodigoTipo(crudo.trim().toUpperCase(Locale.ROOT));
  }

  @Override
  public String toString() {
    return valor;
  }
}
