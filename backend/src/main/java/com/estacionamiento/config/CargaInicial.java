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

import java.time.LocalDateTime;

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

            LocalDateTime ahora = LocalDateTime.now();

            // Estancias cerradas con duracion real, para que el informe de pagos muestre montos.
            cerrada("RES-100", ahora.minusHours(30), ahora.minusHours(28));
            cerrada("RES-100", ahora.minusHours(10), ahora.minusHours(9));
            cerrada("RES-200", ahora.minusHours(20), ahora.minusMinutes(1155));
            cerrada("VIS-100", ahora.minusHours(3), ahora.minusMinutes(150));
            cerrada("VIS-200", ahora.minusHours(5), ahora.minusMinutes(288));
            cerrada("OFI-200", ahora.minusHours(8), ahora.minusMinutes(390));

            // Dos vehiculos siguen dentro del estacionamiento.
            servicioEstancias.registrarEntradaEn("OFI-100", ahora.minusHours(1));
            servicioEstancias.registrarEntradaEn("RES-200", ahora.minusMinutes(20));

            log.info("Datos de ejemplo cargados: 6 vehiculos, 6 estancias cerradas y 2 abiertas");
        };
    }

    private void cerrada(String placa, LocalDateTime entrada, LocalDateTime salida) {
        servicioEstancias.registrarEntradaEn(placa, entrada);
        servicioEstancias.registrarSalidaEn(placa, salida);
    }
}
