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

//Servicio-Estancias
@Service
@RequiredArgsConstructor
public class ServicioEstancias {

    private static final BigDecimal SIN_COBRO = new BigDecimal("0.00");

    private final RepositorioEstancia repositorioEstancia;
    private final RepositorioResidente repositorioResidente;
    private final ServicioVehiculos servicioVehiculos;
    private final ServicioPeriodo servicioPeriodo;
    private final ResolutorTarifas resolutorTarifas;

    //Registro-Entrada
    @Transactional
    public Estancia registrarEntrada(String placa) {
        return registrarEntradaEn(placa, LocalDateTime.now());
    }

    //Entrada-ConFecha
    @Transactional
    public Estancia registrarEntradaEn(String placa, LocalDateTime entrada) {
        Vehiculo vehiculo = servicioVehiculos.buscar(placa);
        //Revision-Duplicado
        repositorioEstancia.findByVehiculoAndSalidaIsNull(vehiculo).ifPresent(abierta -> {
            throw new ReglaDeNegocio("El vehiculo " + vehiculo.getPlaca() + " ya se encuentra dentro del estacionamiento");
        });
        return repositorioEstancia.save(
                Estancia.abrir(vehiculo, entrada, servicioPeriodo.numeroActual()));
    }

    //Registro-Salida
    @Transactional
    public Estancia registrarSalida(String placa) {
        return registrarSalidaEn(placa, LocalDateTime.now());
    }

    //Salida-ConFecha
    @Transactional
    public Estancia registrarSalidaEn(String placa, LocalDateTime salida) {
        Vehiculo vehiculo = servicioVehiculos.buscar(placa);
        Estancia estancia = repositorioEstancia.findByVehiculoAndSalidaIsNull(vehiculo)
                .orElseThrow(() -> new ReglaDeNegocio(
                        "El vehiculo " + vehiculo.getPlaca() + " no tiene una entrada abierta"));

        long minutos = Minutos.entre(estancia.getEntrada(), salida);
        //Cobro-PorTipo
        Tarifa tarifa = resolutorTarifas.para(vehiculo.getTipo());

        repositorioResidente.findByVehiculo(vehiculo)
                .ifPresent(residente -> residente.sumarMinutos(minutos));

        BigDecimal monto = tarifa.seCobraAlSalir() ? tarifa.calcular(minutos) : SIN_COBRO;
        estancia.cerrar(salida, minutos, monto);
        return repositorioEstancia.save(estancia);
    }

    //Historial-Vehiculo
    @Transactional(readOnly = true)
    public List<Estancia> historial(Vehiculo vehiculo) {
        return repositorioEstancia.findByVehiculoOrderByEntradaDesc(vehiculo);
    }

    //Estado-Vehiculo
    @Transactional(readOnly = true)
    public boolean estaDentro(Vehiculo vehiculo) {
        return repositorioEstancia.findByVehiculoAndSalidaIsNull(vehiculo).isPresent();
    }
}
