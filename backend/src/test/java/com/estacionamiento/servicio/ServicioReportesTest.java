package com.estacionamiento.servicio;

import com.estacionamiento.dominio.TipoVehiculo;
import com.estacionamiento.repositorio.RepositorioResidente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ServicioReportesTest {

    @Autowired
    private ServicioReportes servicioReportes;

    @Autowired
    private ServicioVehiculos servicioVehiculos;

    @Autowired
    private RepositorioResidente repositorioResidente;

    @Test
    void cobra_cinco_centavos_por_minuto_acumulado_a_cada_residente() {
        var uno = servicioVehiculos.alta("REP-001", TipoVehiculo.RESIDENTE, "Aldo");
        var dos = servicioVehiculos.alta("REP-002", TipoVehiculo.RESIDENTE, "Ana");
        repositorioResidente.findByVehiculo(uno).orElseThrow().sumarMinutos(100L);
        repositorioResidente.findByVehiculo(dos).orElseThrow().sumarMinutos(40L);

        var informe = servicioReportes.informePagos();

        var renglonUno = informe.renglones().stream()
                .filter(r -> r.placa().equals("REP001")).findFirst().orElseThrow();
        assertThat(renglonUno.minutos()).isEqualTo(100L);
        assertThat(renglonUno.monto()).isEqualByComparingTo("5.00");
        assertThat(informe.total()).isGreaterThanOrEqualTo(new java.math.BigDecimal("7.00"));
    }

    @Test
    void el_informe_incluye_residentes_sin_minutos_con_monto_en_cero() {
        servicioVehiculos.alta("REP-003", TipoVehiculo.RESIDENTE, "Sin uso");

        var informe = servicioReportes.informePagos();

        var renglon = informe.renglones().stream()
                .filter(r -> r.placa().equals("REP003")).findFirst().orElseThrow();
        assertThat(renglon.monto()).isEqualByComparingTo("0.00");
    }

    @Test
    void el_informe_no_incluye_vehiculos_que_no_son_residentes() {
        servicioVehiculos.alta("REP-004", TipoVehiculo.NO_RESIDENTE, "Visita");

        var informe = servicioReportes.informePagos();

        assertThat(informe.renglones()).noneMatch(r -> r.placa().equals("REP004"));
    }
}
