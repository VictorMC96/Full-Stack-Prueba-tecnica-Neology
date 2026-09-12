package com.estacionamiento.repositorio;

import com.estacionamiento.dominio.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//Consultas-Vehiculo
public interface RepositorioVehiculo extends JpaRepository<Vehiculo, Long> {
    Optional<Vehiculo> findByPlaca(String placa);
    boolean existsByPlaca(String placa);
    Page<Vehiculo> findByPlacaContainingIgnoreCase(String placa, Pageable pageable);
}
