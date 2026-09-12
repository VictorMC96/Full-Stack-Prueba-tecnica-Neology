package com.estacionamiento.tarifa;

import com.estacionamiento.dominio.TipoVehiculo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TarifaTest {

    private final ResolutorTarifas resolutor = new ResolutorTarifas(
            List.of(new TarifaOficial(), new TarifaResidente(), new TarifaNoResidente()));

    @Test
    void el_vehiculo_oficial_nunca_paga() {
        var tarifa = resolutor.para(TipoVehiculo.OFICIAL);

        assertThat(tarifa.calcular(1000L)).isEqualByComparingTo("0.00");
    }

    @Test
    void el_residente_paga_cinco_centavos_por_minuto() {
        var tarifa = resolutor.para(TipoVehiculo.RESIDENTE);

        assertThat(tarifa.calcular(100L)).isEqualByComparingTo("5.00");
        assertThat(tarifa.seCobraAlSalir()).isFalse();
    }

    @Test
    void el_no_residente_paga_cincuenta_centavos_por_minuto_al_salir() {
        var tarifa = resolutor.para(TipoVehiculo.NO_RESIDENTE);

        assertThat(tarifa.calcular(10L)).isEqualByComparingTo("5.00");
        assertThat(tarifa.seCobraAlSalir()).isTrue();
    }

    @Test
    void los_montos_traen_siempre_dos_decimales() {
        assertThat(resolutor.para(TipoVehiculo.RESIDENTE).calcular(1L))
                .isEqualTo(new BigDecimal("0.05"));
    }

    @Test
    void existe_una_tarifa_registrada_para_cada_tipo_de_vehiculo() {
        for (TipoVehiculo tipo : TipoVehiculo.values()) {
            assertThat(resolutor.para(tipo)).isNotNull();
        }
    }

    @Test
    void avisa_si_se_pide_la_tarifa_de_un_tipo_sin_implementacion() {
        var incompleto = new ResolutorTarifas(List.of(new TarifaOficial()));

        assertThatThrownBy(() -> incompleto.para(TipoVehiculo.RESIDENTE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("RESIDENTE");
    }
}
