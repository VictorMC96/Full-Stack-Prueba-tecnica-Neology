package com.estacionamiento.servicio;

import com.estacionamiento.dominio.Residente;
import com.estacionamiento.repositorio.RepositorioResidente;
import com.estacionamiento.tarifa.ResolutorTarifas;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

//Servicio-Reportes
@Service
@RequiredArgsConstructor
public class ServicioReportes {

    private final RepositorioResidente repositorioResidente;
    private final ResolutorTarifas resolutorTarifas;
    private final ServicioPeriodo servicioPeriodo;

    //Renglon-Informe
    public record RenglonPago(String placa, String propietario, long minutos, BigDecimal monto) {
    }

    //Informe-Completo
    public record Informe(int periodo, List<RenglonPago> renglones, BigDecimal total) {
    }

    //Generacion-Informe
    @Transactional(readOnly = true)
    public Informe informePagos() {
        List<RenglonPago> renglones = repositorioResidente.findAll().stream()
                .sorted(Comparator.comparing(residente -> residente.getVehiculo().getPlaca()))
                .map(this::aRenglon)
                .toList();

        //Suma-Total
        BigDecimal total = renglones.stream()
                .map(RenglonPago::monto)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);

        return new Informe(servicioPeriodo.numeroActual(), renglones, total);
    }

    //Armado-Renglon
    private RenglonPago aRenglon(Residente residente) {
        long minutos = residente.getMinutosAcumulados();
        BigDecimal monto = resolutorTarifas.para(residente.getVehiculo().getTipo()).calcular(minutos);
        return new RenglonPago(
                residente.getVehiculo().getPlaca(),
                residente.getVehiculo().getPropietario(),
                minutos,
                monto);
    }
}
