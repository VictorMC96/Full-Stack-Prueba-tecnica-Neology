package com.estacionamiento.config;

import com.estacionamiento.dominio.TipoVehiculo;
import com.estacionamiento.repositorio.RepositorioVehiculo;
import com.estacionamiento.servicio.ServicioEstancias;
import com.estacionamiento.servicio.ServicioVehiculos;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CargaInicial {

    private final RepositorioVehiculo repositorioVehiculo;
    private final ServicioVehiculos servicioVehiculos;
    private final ServicioEstancias servicioEstancias;

    @Bean
    public ApplicationRunner datosDeEjemplo() {
        return argumentos -> {
            if (repositorioVehiculo.count() > 0) {
                return;
            }
            servicioVehiculos.alta("OFI-100", TipoVehiculo.OFICIAL, "Patrulla municipal");
            servicioVehiculos.alta("OFI-200", TipoVehiculo.OFICIAL, "Ambulancia");
            servicioVehiculos.alta("RES-100", TipoVehiculo.RESIDENTE, "Aldo Zetina");
            servicioVehiculos.alta("RES-200", TipoVehiculo.RESIDENTE, "Ana Torres");
            servicioVehiculos.alta("VIS-100", TipoVehiculo.NO_RESIDENTE, "Visitante");
            servicioVehiculos.alta("VIS-200", TipoVehiculo.NO_RESIDENTE, "Proveedor");

            servicioEstancias.registrarEntrada("RES-100");
            servicioEstancias.registrarSalida("RES-100");
            servicioEstancias.registrarEntrada("VIS-100");
            servicioEstancias.registrarSalida("VIS-100");

            servicioEstancias.registrarEntrada("OFI-100");
            servicioEstancias.registrarEntrada("RES-200");

            log.info("Datos de ejemplo cargados: 6 vehiculos, 2 estancias cerradas y 2 abiertas");
        };
    }
}
