package com.estacionamiento.servicio;

import java.time.Duration;
import java.time.LocalDateTime;

//Calculo-Minutos
public final class Minutos {

    private Minutos() {
    }

    //Minutos-Transcurridos
    public static long entre(LocalDateTime entrada, LocalDateTime salida) {
        long segundos = Duration.between(entrada, salida).getSeconds();
        //Validacion-Rango
        if (segundos < 0) {
            throw new IllegalArgumentException("La salida no puede ser anterior a la entrada");
        }
        return (segundos + 59) / 60;
    }
}
