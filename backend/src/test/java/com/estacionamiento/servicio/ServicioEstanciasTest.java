package com.estacionamiento.servicio;

import com.estacionamiento.dominio.TipoVehiculo;
import com.estacionamiento.error.RecursoNoEncontrado;
import com.estacionamiento.error.ReglaDeNegocio;
import com.estacionamiento.repositorio.RepositorioResidente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ServicioEstanciasTest {

    @Autowired
    private ServicioEstancias servicioEstancias;

    @Autowired
    private ServicioVehiculos servicioVehiculos;

    @Autowired
    private RepositorioResidente repositorioResidente;

    @Test
    void registra_la_entrada_de_un_vehiculo_y_la_deja_abierta() {
        servicioVehiculos.alta("ENT-001", TipoVehiculo.NO_RESIDENTE, "Visita");

        var estancia = servicioEstancias.registrarEntrada("ENT-001");

        assertThat(estancia.estaAbierta()).isTrue();
        assertThat(estancia.getEntrada()).isNotNull();
        assertThat(estancia.getPeriodo()).isPositive();
    }

    @Test
    void rechaza_una_segunda_entrada_de_un_vehiculo_que_ya_esta_adentro() {
        servicioVehiculos.alta("ENT-002", TipoVehiculo.NO_RESIDENTE, "Visita");
        servicioEstancias.registrarEntrada("ENT-002");

        assertThatThrownBy(() -> servicioEstancias.registrarEntrada("ENT-002"))
                .isInstanceOf(ReglaDeNegocio.class)
                .hasMessageContaining("ya se encuentra");
    }

    @Test
    void rechaza_la_salida_de_un_vehiculo_que_no_esta_adentro() {
        servicioVehiculos.alta("SAL-001", TipoVehiculo.NO_RESIDENTE, "Visita");

        assertThatThrownBy(() -> servicioEstancias.registrarSalida("SAL-001"))
                .isInstanceOf(ReglaDeNegocio.class)
                .hasMessageContaining("no tiene una entrada");
    }

    @Test
    void avisa_si_la_placa_no_esta_registrada() {
        assertThatThrownBy(() -> servicioEstancias.registrarEntrada("NOEXISTE1"))
                .isInstanceOf(RecursoNoEncontrado.class);
    }

    @Test
    void al_salir_un_oficial_no_se_le_cobra_nada() {
        servicioVehiculos.alta("OFI-001", TipoVehiculo.OFICIAL, "Gobierno");
        servicioEstancias.registrarEntrada("OFI-001");

        var estancia = servicioEstancias.registrarSalida("OFI-001");

        assertThat(estancia.estaAbierta()).isFalse();
        assertThat(estancia.getMonto()).isEqualByComparingTo("0.00");
    }

    @Test
    void al_salir_un_no_residente_se_le_cobra_en_ese_momento() {
        servicioVehiculos.alta("NOR-100", TipoVehiculo.NO_RESIDENTE, "Visita");
        servicioEstancias.registrarEntrada("NOR-100");

        var estancia = servicioEstancias.registrarSalida("NOR-100");

        assertThat(estancia.getMinutos()).isNotNull();
        assertThat(estancia.getMonto()).isEqualByComparingTo(
                new java.math.BigDecimal("0.50").multiply(java.math.BigDecimal.valueOf(estancia.getMinutos())));
    }

    @Test
    void al_salir_un_residente_no_paga_pero_acumula_sus_minutos() {
        var vehiculo = servicioVehiculos.alta("EST-100", TipoVehiculo.RESIDENTE, "Aldo");
        servicioEstancias.registrarEntrada("EST-100");

        var estancia = servicioEstancias.registrarSalida("EST-100");

        assertThat(estancia.getMonto()).isEqualByComparingTo("0.00");
        var residente = repositorioResidente.findByVehiculo(vehiculo).orElseThrow();
        assertThat(residente.getMinutosAcumulados()).isEqualTo(estancia.getMinutos());
    }

    @Test
    void el_historial_devuelve_las_estancias_del_vehiculo() {
        var vehiculo = servicioVehiculos.alta("HIS-001", TipoVehiculo.NO_RESIDENTE, "Visita");
        servicioEstancias.registrarEntrada("HIS-001");
        servicioEstancias.registrarSalida("HIS-001");
        servicioEstancias.registrarEntrada("HIS-001");

        assertThat(servicioEstancias.historial(vehiculo)).hasSize(2);
    }
}
