package com.estacionamiento.servicio;

import com.estacionamiento.dominio.TipoVehiculo;
import com.estacionamiento.repositorio.RepositorioEstancia;
import com.estacionamiento.repositorio.RepositorioResidente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ServicioMesTest {

    @Autowired
    private ServicioMes servicioMes;

    @Autowired
    private ServicioVehiculos servicioVehiculos;

    @Autowired
    private ServicioEstancias servicioEstancias;

    @Autowired
    private RepositorioResidente repositorioResidente;

    @Autowired
    private RepositorioEstancia repositorioEstancia;

    @Test
    void al_iniciar_el_mes_los_acumulados_de_los_residentes_vuelven_a_cero() {
        var vehiculo = servicioVehiculos.alta("MES-001", TipoVehiculo.RESIDENTE, "Aldo");
        repositorioResidente.findByVehiculo(vehiculo).orElseThrow().sumarMinutos(500L);

        servicioMes.iniciar();

        assertThat(repositorioResidente.findByVehiculo(vehiculo).orElseThrow().getMinutosAcumulados())
                .isZero();
    }

    @Test
    void el_vehiculo_que_estaba_adentro_sigue_adentro_despues_del_cierre() {
        servicioVehiculos.alta("MES-002", TipoVehiculo.NO_RESIDENTE, "Visita");
        var entrada = servicioEstancias.registrarEntrada("MES-002");

        servicioMes.iniciar();

        var recargada = repositorioEstancia.findById(entrada.getId()).orElseThrow();
        assertThat(recargada.estaAbierta()).isTrue();
    }

    @Test
    void las_estancias_cerradas_se_conservan_con_el_periodo_anterior() {
        servicioVehiculos.alta("MES-003", TipoVehiculo.NO_RESIDENTE, "Visita");
        servicioEstancias.registrarEntrada("MES-003");
        var cerrada = servicioEstancias.registrarSalida("MES-003");
        int periodoPrevio = cerrada.getPeriodo();

        int periodoNuevo = servicioMes.iniciar();

        assertThat(periodoNuevo).isEqualTo(periodoPrevio + 1);
        assertThat(repositorioEstancia.findById(cerrada.getId()).orElseThrow().getPeriodo())
                .isEqualTo(periodoPrevio);
    }

    @Test
    void las_entradas_registradas_despues_del_cierre_llevan_el_periodo_nuevo() {
        servicioVehiculos.alta("MES-004", TipoVehiculo.NO_RESIDENTE, "Visita");

        int periodoNuevo = servicioMes.iniciar();
        var estancia = servicioEstancias.registrarEntrada("MES-004");

        assertThat(estancia.getPeriodo()).isEqualTo(periodoNuevo);
    }
}
