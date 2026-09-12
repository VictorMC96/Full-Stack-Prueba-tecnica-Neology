package com.estacionamiento.web;

import com.estacionamiento.servicio.ServicioEstancias;
import com.estacionamiento.servicio.ServicioReportes;
import com.estacionamiento.servicio.ServicioVehiculos;
import com.estacionamiento.web.dto.Respuestas;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

//Mapeo-Respuestas
@Component
@RequiredArgsConstructor
public class Mapeador {

    private final ServicioVehiculos servicioVehiculos;
    private final ServicioEstancias servicioEstancias;

    //Mapeo-Vehiculo
    public Respuestas.Vehiculo aVehiculo(com.estacionamiento.dominio.Vehiculo vehiculo) {
        return new Respuestas.Vehiculo(
                vehiculo.getPlaca(),
                vehiculo.getTipo().name(),
                vehiculo.getPropietario(),
                vehiculo.getAlta(),
                servicioEstancias.estaDentro(vehiculo),
                servicioVehiculos.minutosAcumulados(vehiculo));
    }

    //Mapeo-Estancia
    public Respuestas.Estancia aEstancia(com.estacionamiento.dominio.Estancia estancia) {
        return new Respuestas.Estancia(
                estancia.getId(),
                estancia.getVehiculo().getPlaca(),
                estancia.getEntrada(),
                estancia.getSalida(),
                estancia.getMinutos(),
                estancia.getMonto(),
                estancia.getPeriodo());
    }

    //Mapeo-Informe
    public Respuestas.InformePagos aInforme(ServicioReportes.Informe informe) {
        List<Respuestas.RenglonPago> renglones = informe.renglones().stream()
                .map(renglon -> new Respuestas.RenglonPago(
                        renglon.placa(), renglon.propietario(), renglon.minutos(), renglon.monto()))
                .toList();
        return new Respuestas.InformePagos(informe.periodo(), renglones, informe.total());
    }
}
