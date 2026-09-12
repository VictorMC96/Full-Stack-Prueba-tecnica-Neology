package com.estacionamiento.servicio;

import com.estacionamiento.dominio.TipoVehiculo;
import com.estacionamiento.error.RecursoNoEncontrado;
import com.estacionamiento.error.ReglaDeNegocio;
import com.estacionamiento.error.SolicitudInvalida;
import com.estacionamiento.repositorio.RepositorioResidente;
import com.estacionamiento.repositorio.RepositorioVehiculo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ServicioVehiculosTest {

    @Autowired
    private ServicioVehiculos servicio;

    @Autowired
    private RepositorioVehiculo repositorioVehiculo;

    @Autowired
    private RepositorioResidente repositorioResidente;

    @Test
    void da_de_alta_un_vehiculo_oficial() {
        var vehiculo = servicio.alta("abc 123", TipoVehiculo.OFICIAL, "Gobierno");

        assertThat(vehiculo.getPlaca()).isEqualTo("ABC123");
        assertThat(vehiculo.getTipo()).isEqualTo(TipoVehiculo.OFICIAL);
    }

    @Test
    void al_dar_de_alta_un_residente_crea_tambien_su_acumulado_en_cero() {
        var vehiculo = servicio.alta("RES-001", TipoVehiculo.RESIDENTE, "Aldo");

        var residente = repositorioResidente.findByVehiculo(vehiculo);
        assertThat(residente).isPresent();
        assertThat(residente.get().getMinutosAcumulados()).isZero();
    }

    @Test
    void un_vehiculo_que_no_es_residente_no_genera_acumulado() {
        var vehiculo = servicio.alta("NOR-001", TipoVehiculo.NO_RESIDENTE, "Visita");

        assertThat(repositorioResidente.findByVehiculo(vehiculo)).isEmpty();
    }

    @Test
    void rechaza_dar_de_alta_dos_veces_la_misma_placa() {
        servicio.alta("DUP-001", TipoVehiculo.OFICIAL, "Uno");

        assertThatThrownBy(() -> servicio.alta("dup 001", TipoVehiculo.RESIDENTE, "Dos"))
                .isInstanceOf(ReglaDeNegocio.class)
                .hasMessageContaining("DUP001");
    }

    @Test
    void rechaza_una_placa_con_formato_invalido() {
        assertThatThrownBy(() -> servicio.alta("a!", TipoVehiculo.OFICIAL, "Nadie"))
                .isInstanceOf(SolicitudInvalida.class);
    }

    @Test
    void rechaza_una_placa_que_al_quitar_el_guion_queda_por_debajo_del_minimo() {
        assertThatThrownBy(() -> servicio.alta("AB-12", TipoVehiculo.OFICIAL, "Corta"))
                .isInstanceOf(SolicitudInvalida.class);
    }

    @Test
    void rechaza_una_placa_que_excede_el_maximo() {
        assertThatThrownBy(() -> servicio.alta("ABCDEFGHIJKL", TipoVehiculo.OFICIAL, "Larga"))
                .isInstanceOf(SolicitudInvalida.class);
    }

    @Test
    void avisa_cuando_la_placa_buscada_no_existe() {
        assertThatThrownBy(() -> servicio.buscar("FANTASMA1"))
                .isInstanceOf(RecursoNoEncontrado.class);
    }

    @Test
    void filtra_el_listado_por_coincidencia_parcial_de_placa() {
        servicio.alta("AAA-111", TipoVehiculo.OFICIAL, "Uno");
        servicio.alta("BBB-222", TipoVehiculo.OFICIAL, "Dos");

        var pagina = servicio.listar("aaa", org.springframework.data.domain.PageRequest.of(0, 10));

        assertThat(pagina.getContent()).hasSize(1);
        assertThat(pagina.getContent().get(0).getPlaca()).isEqualTo("AAA111");
        assertThat(repositorioVehiculo.count()).isGreaterThanOrEqualTo(2);
    }
}
