package com.estacionamiento.servicio;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MinutosTest {

    private final LocalDateTime entrada = LocalDateTime.of(2026, 9, 11, 10, 0, 0);

    @Test
    void cuenta_los_minutos_exactos() {
        assertThat(Minutos.entre(entrada, entrada.plusMinutes(30))).isEqualTo(30L);
    }

    @Test
    void redondea_hacia_arriba_cualquier_fraccion_de_minuto() {
        assertThat(Minutos.entre(entrada, entrada.plusSeconds(61))).isEqualTo(2L);
        assertThat(Minutos.entre(entrada, entrada.plusSeconds(1))).isEqualTo(1L);
    }

    @Test
    void una_estancia_sin_duracion_cuenta_cero_minutos() {
        assertThat(Minutos.entre(entrada, entrada)).isEqualTo(0L);
    }

    @Test
    void rechaza_una_salida_anterior_a_la_entrada() {
        assertThatThrownBy(() -> Minutos.entre(entrada, entrada.minusMinutes(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
