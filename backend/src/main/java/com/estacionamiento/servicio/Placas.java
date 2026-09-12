package com.estacionamiento.servicio;

import com.estacionamiento.error.SolicitudInvalida;

import java.util.regex.Pattern;

public final class Placas {

    public static final int LARGO_MINIMO = 5;
    public static final int LARGO_MAXIMO = 10;

    private static final Pattern FORMATO =
            Pattern.compile("^[A-Z0-9]{" + LARGO_MINIMO + "," + LARGO_MAXIMO + "}$");

    private Placas() {
    }

    public static String normalizar(String placa) {
        if (placa == null) {
            throw new SolicitudInvalida("La placa es obligatoria");
        }
        String limpia = limpiar(placa);
        if (!FORMATO.matcher(limpia).matches()) {
            throw new SolicitudInvalida("La placa " + placa + " debe tener entre " + LARGO_MINIMO
                    + " y " + LARGO_MAXIMO + " letras o numeros, sin contar espacios ni guiones");
        }
        return limpia;
    }

    public static String limpiar(String placa) {
        return placa == null ? "" : placa.trim().toUpperCase().replace(" ", "").replace("-", "");
    }
}
