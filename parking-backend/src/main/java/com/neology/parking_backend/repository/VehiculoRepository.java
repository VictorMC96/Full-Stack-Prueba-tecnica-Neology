package com.neology.parking_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.neology.parking_backend.model.Vehiculo;

public interface VehiculoRepository extends JpaRepository<Vehiculo, String>{

}
