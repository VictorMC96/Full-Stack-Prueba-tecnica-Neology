package com.estacionamiento.servicio;

import com.estacionamiento.dominio.PeriodoActual;
import com.estacionamiento.repositorio.RepositorioPeriodo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//Servicio-Periodo
@Service
@RequiredArgsConstructor
public class ServicioPeriodo {

    private static final long UNICO = 1L;

    private final RepositorioPeriodo repositorio;

    //Periodo-Vigente
    @Transactional
    public PeriodoActual actual() {
        return repositorio.findById(UNICO).orElseGet(() -> repositorio.save(PeriodoActual.inicial()));
    }

    @Transactional
    public int numeroActual() {
        return actual().getNumero();
    }

    //Avance-Periodo
    @Transactional
    public int avanzar() {
        PeriodoActual periodo = actual();
        periodo.avanzar();
        return repositorio.save(periodo).getNumero();
    }
}
