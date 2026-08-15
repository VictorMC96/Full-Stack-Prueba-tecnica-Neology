package com.neology.parking_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neology.parking_backend.model.Estancia;

public interface EstanciaRepository extends JpaRepository<Estancia, Long>{

    Optional<Estancia> findByVehiculo_PlacaAndFechaSalidaIsNull(String placa);

    List<Estancia> findByVehiculo_PlacaOrderByFechaEntradaDesc(String placa);

    List<Estancia> findByFechaSalidaIsNull();

}
