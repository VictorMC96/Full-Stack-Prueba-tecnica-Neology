package mx.neology.acceso.domain;

import java.util.Locale;
import java.util.regex.Pattern;
import mx.neology.acceso.domain.excepcion.PlacaInvalidaException;

/** Placa normalizada: mayúsculas, sin espacios ni guiones, de 3 a 10 letras o dígitos. */
public record Placa(String valor) {

  private static final Pattern FORMATO = Pattern.compile("^[A-Z0-9]{3,10}$");

  public Placa {
    if (valor == null || !FORMATO.matcher(valor).matches()) {
      throw new PlacaInvalidaException(valor);
    }
  }

  /** Normaliza la placa cruda y la valida; lanza {@link PlacaInvalidaException} si no cumple. */
  public static Placa de(String cruda) {
    if (cruda == null) {
      throw new PlacaInvalidaException(null);
    }
    return new Placa(normalizar(cruda));
  }

  /** Normaliza sin validar (útil para filtros parciales). */
  public static String normalizar(String cruda) {
    if (cruda == null) {
      return "";
    }
    return cruda.toUpperCase(Locale.ROOT).replaceAll("[\\s-]", "");
  }

  @Override
  public String toString() {
    return valor;
  }
}
