package com.estacionamiento.servicio;

import com.estacionamiento.dominio.Residente;
import com.estacionamiento.repositorio.RepositorioResidente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//Cierre-Mes
@Service
@RequiredArgsConstructor
public class ServicioMes {

    private final RepositorioResidente repositorioResidente;
    private final ServicioPeriodo servicioPeriodo;

    //Reinicio-Acumulados
    @Transactional
    public int iniciar() {
        List<Residente> residentes = repositorioResidente.findAll();
        residentes.forEach(Residente::reiniciar);
        repositorioResidente.saveAll(residentes);
        return servicioPeriodo.avanzar();
    }
}
