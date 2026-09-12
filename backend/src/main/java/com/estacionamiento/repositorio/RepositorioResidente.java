package com.estacionamiento.repositorio;

import com.estacionamiento.dominio.Residente;
import com.estacionamiento.dominio.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositorioResidente extends JpaRepository<Residente, Long> {
    Optional<Residente> findByVehiculo(Vehiculo vehiculo);
}
