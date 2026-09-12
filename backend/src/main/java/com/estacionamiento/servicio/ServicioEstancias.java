package com.estacionamiento.servicio;

import com.estacionamiento.dominio.Estancia;
import com.estacionamiento.dominio.Vehiculo;
import com.estacionamiento.error.ReglaDeNegocio;
import com.estacionamiento.repositorio.RepositorioEstancia;
import com.estacionamiento.repositorio.RepositorioResidente;
import com.estacionamiento.tarifa.ResolutorTarifas;
import com.estacionamiento.tarifa.Tarifa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioEstancias {

    private static final BigDecimal SIN_COBRO = new BigDecimal("0.00");

    private final RepositorioEstancia repositorioEstancia;
    private final RepositorioResidente repositorioResidente;
    private final ServicioVehiculos servicioVehiculos;
    private final ServicioPeriodo servicioPeriodo;
    private final ResolutorTarifas resolutorTarifas;

    @Transactional
    public Estancia registrarEntrada(String placa) {
        Vehiculo vehiculo = servicioVehiculos.buscar(placa);
        repositorioEstancia.findByVehiculoAndSalidaIsNull(vehiculo).ifPresent(abierta -> {
            throw new ReglaDeNegocio("El vehiculo " + vehiculo.getPlaca() + " ya se encuentra dentro del estacionamiento");
        });
        return repositorioEstancia.save(
                Estancia.abrir(vehiculo, LocalDateTime.now(), servicioPeriodo.numeroActual()));
    }

    @Transactional
    public Estancia registrarSalida(String placa) {
        Vehiculo vehiculo = servicioVehiculos.buscar(placa);
        Estancia estancia = repositorioEstancia.findByVehiculoAndSalidaIsNull(vehiculo)
                .orElseThrow(() -> new ReglaDeNegocio(
                        "El vehiculo " + vehiculo.getPlaca() + " no tiene una entrada abierta"));

        LocalDateTime salida = LocalDateTime.now();
        long minutos = Minutos.entre(estancia.getEntrada(), salida);
        Tarifa tarifa = resolutorTarifas.para(vehiculo.getTipo());

        repositorioResidente.findByVehiculo(vehiculo)
                .ifPresent(residente -> residente.sumarMinutos(minutos));

        BigDecimal monto = tarifa.seCobraAlSalir() ? tarifa.calcular(minutos) : SIN_COBRO;
        estancia.cerrar(salida, minutos, monto);
        return repositorioEstancia.save(estancia);
    }

    @Transactional(readOnly = true)
    public List<Estancia> historial(Vehiculo vehiculo) {
        return repositorioEstancia.findByVehiculoOrderByEntradaDesc(vehiculo);
    }

    @Transactional(readOnly = true)
    public boolean estaDentro(Vehiculo vehiculo) {
        return repositorioEstancia.findByVehiculoAndSalidaIsNull(vehiculo).isPresent();
    }
}
