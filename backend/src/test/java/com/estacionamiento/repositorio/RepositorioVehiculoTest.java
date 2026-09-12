package com.estacionamiento.repositorio;

import com.estacionamiento.dominio.TipoVehiculo;
import com.estacionamiento.dominio.Vehiculo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RepositorioVehiculoTest {

    @Autowired
    private RepositorioVehiculo repositorio;

    @Test
    void guarda_y_recupera_un_vehiculo_por_su_placa() {
        repositorio.save(Vehiculo.de("ABC-123", TipoVehiculo.RESIDENTE, "Aldo"));

        var encontrado = repositorio.findByPlaca("ABC-123");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getTipo()).isEqualTo(TipoVehiculo.RESIDENTE);
        assertThat(encontrado.get().getPropietario()).isEqualTo("Aldo");
    }

    @Test
    void reporta_si_una_placa_ya_existe() {
        repositorio.save(Vehiculo.de("XYZ-999", TipoVehiculo.OFICIAL, "Gobierno"));

        assertThat(repositorio.existsByPlaca("XYZ-999")).isTrue();
        assertThat(repositorio.existsByPlaca("NO-EXISTE")).isFalse();
    }
}
