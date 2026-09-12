package com.estacionamiento.config;

import com.estacionamiento.dominio.TipoVehiculo;
import com.estacionamiento.repositorio.RepositorioEstancia;
import com.estacionamiento.repositorio.RepositorioResidente;
import com.estacionamiento.repositorio.RepositorioVehiculo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CargaInicialTest {

    @Autowired
    private RepositorioVehiculo repositorioVehiculo;

    @Autowired
    private RepositorioEstancia repositorioEstancia;

    @Autowired
    private RepositorioResidente repositorioResidente;

    @Test
    void la_base_arranca_con_vehiculos_de_los_tres_tipos() {
        assertThat(repositorioVehiculo.findAll())
                .extracting(vehiculo -> vehiculo.getTipo())
                .contains(TipoVehiculo.OFICIAL, TipoVehiculo.RESIDENTE, TipoVehiculo.NO_RESIDENTE);
    }

    @Test
    void la_base_arranca_con_al_menos_una_estancia_cerrada_y_una_abierta() {
        var estancias = repositorioEstancia.findAll();

        assertThat(estancias).anyMatch(estancia -> !estancia.estaAbierta());
        assertThat(estancias).anyMatch(estancia -> estancia.estaAbierta());
    }

    @Test
    void los_residentes_arrancan_con_minutos_acumulados_reales() {
        var residentes = repositorioResidente.findAll();

        assertThat(residentes).isNotEmpty();
        assertThat(residentes).allMatch(residente -> residente.getMinutosAcumulados() > 0);
    }

    @Test
    void las_estancias_cerradas_de_ejemplo_duran_mas_de_un_minuto() {
        var cerradas = repositorioEstancia.findAll().stream()
                .filter(estancia -> !estancia.estaAbierta())
                .toList();

        assertThat(cerradas).isNotEmpty();
        assertThat(cerradas).allMatch(estancia -> estancia.getMinutos() > 1);
    }
}
