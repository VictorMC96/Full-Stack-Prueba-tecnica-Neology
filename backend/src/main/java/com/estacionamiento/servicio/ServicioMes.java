package com.estacionamiento.servicio;

import com.estacionamiento.dominio.Residente;
import com.estacionamiento.repositorio.RepositorioResidente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioMes {

    private final RepositorioResidente repositorioResidente;
    private final ServicioPeriodo servicioPeriodo;

    @Transactional
    public int iniciar() {
        List<Residente> residentes = repositorioResidente.findAll();
        residentes.forEach(Residente::reiniciar);
        repositorioResidente.saveAll(residentes);
        return servicioPeriodo.avanzar();
    }
}
