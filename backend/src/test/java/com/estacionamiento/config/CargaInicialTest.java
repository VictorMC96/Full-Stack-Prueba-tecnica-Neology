package com.estacionamiento.config;

import com.estacionamiento.dominio.TipoVehiculo;
import com.estacionamiento.repositorio.RepositorioEstancia;
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
}
