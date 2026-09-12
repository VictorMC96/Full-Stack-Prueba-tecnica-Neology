package com.estacionamiento.servicio;

import com.estacionamiento.error.ReglaDeNegocio;

import java.util.regex.Pattern;

public final class Placas {

    private static final Pattern FORMATO = Pattern.compile("^[A-Z0-9-]{5,10}$");

    private Placas() {
    }

    public static String normalizar(String placa) {
        if (placa == null) {
            throw new ReglaDeNegocio("La placa es obligatoria");
        }
        String limpia = placa.trim().toUpperCase().replace(" ", "");
        if (!FORMATO.matcher(limpia).matches()) {
            throw new ReglaDeNegocio("La placa " + placa + " no tiene un formato valido");
        }
        return limpia.replace("-", "");
    }
}
