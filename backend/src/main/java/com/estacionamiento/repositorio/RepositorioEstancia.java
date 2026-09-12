package com.estacionamiento.repositorio;

import com.estacionamiento.dominio.Estancia;
import com.estacionamiento.dominio.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepositorioEstancia extends JpaRepository<Estancia, Long> {
    Optional<Estancia> findByVehiculoAndSalidaIsNull(Vehiculo vehiculo);
    List<Estancia> findByVehiculoOrderByEntradaDesc(Vehiculo vehiculo);
}
